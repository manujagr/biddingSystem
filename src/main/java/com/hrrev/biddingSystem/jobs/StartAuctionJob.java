package com.hrrev.biddingSystem.jobs;

import com.hrrev.biddingSystem.events.AuctionStartedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.notification.UserNotification;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
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
public class StartAuctionJob implements Job {

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

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

        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.SCHEDULED) {
            slot.setStatus(AuctionSlot.SlotStatus.ACTIVE);
            auctionSlotRepository.save(slot);

            // Publish event to Kafka
            AuctionStartedEvent event = new AuctionStartedEvent(slot.getSlotId(), slot.getProduct().getProductId());
            kafkaTemplate.send("auction-started", event);

            // Prepare notification message
            NotificationMessage message = new NotificationMessage(
                    MessageType.AUCTION_STARTED,
                    "Auction Started",
                    "The auction for " + slot.getProduct().getName() + " has started."
            );

            // Fetch users subscribed to the product's category
            List<User> users = userRepository.findUsersSubscribedToCategory(slot.getProduct().getCategory());

            // Send notification messages to Kafka for subscribed users
            for (User user : users) {
                // Check if the user is subscribed to AUCTION_STARTED notifications
                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, MessageType.AUCTION_STARTED)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                }
            }

            // Notify vendor
            User vendor = slot.getProduct().getVendor().getUser();
            NotificationMessage vendorMessage = new NotificationMessage(
                    MessageType.VENDOR_NOTIFICATION,
                    "Your Auction Has Started",
                    "Your auction for " + slot.getProduct().getName() + " has started."
            );

            if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(vendor, MessageType.VENDOR_NOTIFICATION)) {
                kafkaTemplate.send("notification-topic", new UserNotification(vendor.getUserId(), vendorMessage));
            }
        }
    }
}
