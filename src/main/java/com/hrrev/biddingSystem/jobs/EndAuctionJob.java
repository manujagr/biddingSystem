package com.hrrev.biddingSystem.jobs;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.notification.UserNotification;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.repository.UserRepository;
import com.hrrev.biddingSystem.repository.NotificationPreferenceRepository;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EndAuctionJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(EndAuctionJob.class);

    private final AuctionSlotRepository auctionSlotRepository;
    private final BidRepository bidRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public EndAuctionJob(
            AuctionSlotRepository auctionSlotRepository,
            BidRepository bidRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            UserRepository userRepository,
            NotificationPreferenceRepository notificationPreferenceRepository
    ) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.bidRepository = bidRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        logger.info("Executing EndAuctionJob for slot ID: {}", slotId);

        AuctionSlot slot = auctionSlotRepository.findById(slotId)
                .orElse(null);

        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.ACTIVE) {
            slot.setStatus(AuctionSlot.SlotStatus.COMPLETED);
            auctionSlotRepository.save(slot);

            logger.info("Auction slot ID: {} has been marked as completed.", slotId);

            Bid winningBid = bidRepository.findTopBySlotOrderByBidAmountDesc(slot).orElse(null);
            UUID winningBidId = null;
            UUID winnerUserId = null;

            if (winningBid != null) {
                winningBidId = winningBid.getBidId();
                winnerUserId = winningBid.getUser().getUserId();
            }

            AuctionEndedEvent event = new AuctionEndedEvent(
                    slot.getSlotId(),
                    slot.getProduct().getProductId(),
                    winningBidId,
                    winnerUserId
            );
            kafkaTemplate.send("auction-ended", event);
            logger.info("AuctionEndedEvent published to Kafka for slot ID: {}", slot.getSlotId());

            NotificationMessage endedMessage = new NotificationMessage(
                    MessageType.AUCTION_ENDED,
                    "Auction Ended",
                    "The auction for " + slot.getProduct().getName() + " has ended."
            );

            NotificationMessage winnerMessage = new NotificationMessage(
                    MessageType.WINNER_NOTIFICATION,
                    "Congratulations! You Won the Auction",
                    "You have won the auction for " + slot.getProduct().getName() + "!"
            );

            List<User> users = userRepository.findUsersWhoBidOnSlotAndSubscribed(slot, MessageType.AUCTION_ENDED);

            for (User user : users) {
                NotificationMessage message = user.getUserId().equals(winnerUserId) ? winnerMessage : endedMessage;
                MessageType messageType = user.getUserId().equals(winnerUserId) ? MessageType.WINNER_NOTIFICATION : MessageType.AUCTION_ENDED;

                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, messageType)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                    logger.info("Notification sent to user ID: {} for auction slot: {}", user.getUserId(), slot.getSlotId());
                }
            }

            User vendor = slot.getProduct().getVendor().getUser();
            NotificationMessage vendorMessage = new NotificationMessage(
                    MessageType.VENDOR_NOTIFICATION,
                    "Your Auction Has Ended",
                    "Your auction for " + slot.getProduct().getName() + " has ended."
            );

            if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(vendor, MessageType.VENDOR_NOTIFICATION)) {
                kafkaTemplate.send("notification-topic", new UserNotification(vendor.getUserId(), vendorMessage));
                logger.info("Notification sent to vendor ID: {} for auction slot: {}", vendor.getUserId(), slot.getSlotId());
            }
        } else {
            logger.warn("Auction slot ID: {} is not active or does not exist.", slotId);
        }
    }
}