package com.intuit.biddingSystem.auction.auctionslot.jobs;

import com.intuit.biddingSystem.auction.auctionslot.event.AuctionEndedEvent;
import com.intuit.biddingSystem.auction.auctionslot.model.AuctionSlot;
import com.intuit.biddingSystem.auction.auctionslot.repository.AuctionSlotRepository;
import com.intuit.biddingSystem.auction.bid.model.Bid;
import com.intuit.biddingSystem.auction.bid.repository.BidRepository;
import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.notification.UserNotification;
import com.intuit.biddingSystem.notification.repository.NotificationPreferenceRepository;
import com.intuit.biddingSystem.user.model.User;
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
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public EndAuctionJob(
            AuctionSlotRepository auctionSlotRepository,
            BidRepository bidRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            NotificationPreferenceRepository notificationPreferenceRepository
    ) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.bidRepository = bidRepository;
        this.kafkaTemplate = kafkaTemplate;
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
                    NotificationMessage.MessageType.AUCTION_ENDED,
                    "Auction Ended",
                    "The auction for " + slot.getProduct().getName() + " has ended."
            );

            NotificationMessage winnerMessage = new NotificationMessage(
                    NotificationMessage.MessageType.WINNER_NOTIFICATION,
                    "Congratulations! You Won the Auction",
                    "You have won the auction for " + slot.getProduct().getName() + "!"
            );

            //1st attempt to get list of users who have placed a bid
            List<User> bidders = bidRepository.findBidderUsersBySlot(slot);
            List<User> biddersToBeNotified = notificationPreferenceRepository.findBiddersWhoAreSubscribedForMessageType(bidders,
                    NotificationMessage.MessageType.AUCTION_ENDED);


            for (User user : biddersToBeNotified) {
                NotificationMessage message = user.getUserId().equals(winnerUserId) ? winnerMessage : endedMessage;
                NotificationMessage.MessageType messageType = user.getUserId().equals(winnerUserId) ?
                        NotificationMessage.MessageType.WINNER_NOTIFICATION : NotificationMessage.MessageType.AUCTION_ENDED;

                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, messageType)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                    logger.info("Notification sent to user ID: {} for auction slot: {}", user.getUserId(), slot.getSlotId());
                }
            }

            User vendor = slot.getProduct().getVendor().getUser();
            NotificationMessage vendorMessage = new NotificationMessage(
                    NotificationMessage.MessageType.VENDOR_NOTIFICATION,
                    "Your Auction Has Ended",
                    "Your auction for " + slot.getProduct().getName() + " has ended."
            );

            if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(vendor, NotificationMessage.MessageType.VENDOR_NOTIFICATION)) {
                kafkaTemplate.send("notification-topic", new UserNotification(vendor.getUserId(), vendorMessage));
                logger.info("Notification sent to vendor ID: {} for auction slot: {}", vendor.getUserId(), slot.getSlotId());
            }
        } else {
            logger.warn("Auction slot ID: {} is not active or does not exist.", slotId);
        }
    }
}