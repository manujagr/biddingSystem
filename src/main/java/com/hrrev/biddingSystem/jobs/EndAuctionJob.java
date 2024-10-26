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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EndAuctionJob implements Job {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        AuctionSlot slot = auctionSlotRepository.findById(slotId).orElse(null);

        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.ACTIVE) {
            slot.setStatus(AuctionSlot.SlotStatus.COMPLETED);
            auctionSlotRepository.save(slot);

            // Determine winner
            Bid winningBid = bidRepository.findTopBySlotOrderByBidAmountDesc(slot).orElse(null);

            UUID winningBidId = null;
            UUID winnerUserId = null;

            if (winningBid != null) {
                winningBidId = winningBid.getBidId();
                winnerUserId = winningBid.getUser().getUserId();
            }

            // Publish event to Kafka
            AuctionEndedEvent event = new AuctionEndedEvent(
                    slot.getSlotId(),
                    slot.getProduct().getProductId(),
                    winningBidId,
                    winnerUserId
            );
            kafkaTemplate.send("auction-ended", event);

            // Prepare notification messages
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

            // Fetch users who bid on the auction and are subscribed
            List<User> users = userRepository.findUsersWhoBidOnSlotAndSubscribed(slot, MessageType.AUCTION_ENDED);

            for (User user : users) {
                NotificationMessage message = endedMessage;
                MessageType messageType = MessageType.AUCTION_ENDED;

                if (user.getUserId().equals(winnerUserId)) {
                    message = winnerMessage;
                    messageType = MessageType.WINNER_NOTIFICATION;
                }

                // Check user preferences for notification channels and message types
                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, messageType)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                }
            }

            // Notify vendor
            User vendor = slot.getProduct().getVendor().getUser();
            NotificationMessage vendorMessage = new NotificationMessage(
                    MessageType.VENDOR_NOTIFICATION,
                    "Your Auction Has Ended",
                    "Your auction for " + slot.getProduct().getName() + " has ended."
            );

            if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(vendor, MessageType.VENDOR_NOTIFICATION)) {
                kafkaTemplate.send("notification-topic", new UserNotification(vendor.getUserId(), vendorMessage));
            }
        }
    }
}
