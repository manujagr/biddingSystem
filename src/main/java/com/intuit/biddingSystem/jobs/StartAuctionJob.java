package com.intuit.biddingSystem.jobs;

import com.intuit.biddingSystem.events.AuctionStartedEvent;
import com.intuit.biddingSystem.model.AuctionSlot;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.notification.UserNotification;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.repository.AuctionSlotRepository;
import com.intuit.biddingSystem.repository.UserRepository;
import com.intuit.biddingSystem.repository.NotificationPreferenceRepository;
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
public class StartAuctionJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(StartAuctionJob.class);

    private final AuctionSlotRepository auctionSlotRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserRepository userRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;

    @Autowired
    public StartAuctionJob(
            AuctionSlotRepository auctionSlotRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            UserRepository userRepository,
            NotificationPreferenceRepository notificationPreferenceRepository
    ) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.userRepository = userRepository;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
    }

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        logger.info("Executing StartAuctionJob for slot ID: {}", slotId);

        AuctionSlot slot = auctionSlotRepository.findById(slotId).orElse(null);

        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.SCHEDULED) {
            slot.setStatus(AuctionSlot.SlotStatus.ACTIVE);
            auctionSlotRepository.save(slot);

            logger.info("Auction slot ID: {} marked as ACTIVE", slotId);

            // Publish event to Kafka
            AuctionStartedEvent event = new AuctionStartedEvent(slot.getSlotId(), slot.getProduct().getProductId());
            kafkaTemplate.send("auction-started", event);
            logger.info("AuctionStartedEvent published to Kafka for slot ID: {}", slot.getSlotId());

            // Prepare notification message for users
            NotificationMessage message = new NotificationMessage(
                    MessageType.AUCTION_STARTED,
                    "Auction Started",
                    "The auction for " + slot.getProduct().getName() + " has started."
            );

            // Fetch users subscribed to the product's category
            List<User> users = userRepository.findUsersSubscribedToCategory(slot.getProduct().getCategory());
            logger.info("Found {} users subscribed to category: {}", users.size(), slot.getProduct().getCategory());

            // Send notification messages to Kafka for subscribed users
            for (User user : users) {
                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, MessageType.AUCTION_STARTED)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                    logger.info("Notification sent to user ID: {} for auction start", user.getUserId());
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
                logger.info("Vendor notification sent to vendor ID: {} for auction start", vendor.getUserId());
            }
        } else {
            logger.warn("Auction slot ID: {} is either not found or not in SCHEDULED status", slotId);
        }
    }
}