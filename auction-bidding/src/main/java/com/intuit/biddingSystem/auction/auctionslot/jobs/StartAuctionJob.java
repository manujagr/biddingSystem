package com.intuit.biddingSystem.auction.auctionslot.jobs;

import com.intuit.biddingSystem.auction.auctionslot.event.AuctionStartedEvent;
import com.intuit.biddingSystem.auction.auctionslot.model.AuctionSlot;
import com.intuit.biddingSystem.auction.auctionslot.repository.AuctionSlotRepository;
import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.notification.UserNotification;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.notification.repository.NotificationPreferenceRepository;
import com.intuit.biddingSystem.user.model.User;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class StartAuctionJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(StartAuctionJob.class);

    private final AuctionSlotRepository auctionSlotRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public static final String AUCTION_STARTED_TOPIC = "auction-started";
    private static final String AUCTION_DETAILS_KEY = "auction:details:";


    @Autowired
    public StartAuctionJob(
            AuctionSlotRepository auctionSlotRepository,
            KafkaTemplate<String, Object> kafkaTemplate,
            NotificationPreferenceRepository notificationPreferenceRepository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.notificationPreferenceRepository = notificationPreferenceRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void execute(JobExecutionContext context) {
        UUID slotId = UUID.fromString(context.getMergedJobDataMap().getString("slotId"));
        logger.info("Executing StartAuctionJob for slot ID: {}", slotId);
        String key = AUCTION_DETAILS_KEY + slotId.toString();

        AuctionSlot slot = auctionSlotRepository.findById(slotId).orElse(null);
        LocalDateTime endTime = slot.getEndTime();

        if (slot != null && slot.getStatus() == AuctionSlot.SlotStatus.SCHEDULED) {
            slot.setStatus(AuctionSlot.SlotStatus.ACTIVE);
            auctionSlotRepository.save(slot);

            logger.info("Auction slot ID: {} marked as ACTIVE", slotId);
            redisTemplate.opsForHash().put(key, "basePrice", slot.getProduct().getBasePrice());
            redisTemplate.opsForHash().put(key, "endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            // Publish event to Kafka
            AuctionStartedEvent event = new AuctionStartedEvent(slot.getSlotId(), slot.getProduct().getProductId());
            kafkaTemplate.send(AUCTION_STARTED_TOPIC, event);
            logger.info("AuctionStartedEvent published to Kafka for slot ID: {}", slot.getSlotId());

            // Prepare notification message for users
            NotificationMessage message = new NotificationMessage(
                    MessageType.AUCTION_STARTED,
                    "Auction Started",
                    "The auction for " + slot.getProduct().getName() + " has started."
            );

            // Fetch users subscribed to auction started message
            List<User> users = notificationPreferenceRepository
                    .findUserByMessageTypeAndSubscribed(MessageType.AUCTION_STARTED);
            logger.info("Found {} users subscribed to category: {}", users.size(), slot.getProduct().getCategory());

            // Send notification messages to Kafka for subscribed users
            for (User user : users) {
                if (notificationPreferenceRepository.existsByUserAndMessageTypeAndSubscribedTrue(user, MessageType.AUCTION_STARTED)) {
                    kafkaTemplate.send("notification-topic", new UserNotification(user.getUserId(), message));
                    logger.info("Notification sent to user ID: {} for auction start", user.getUserId());
                }
            }

            // Notify vendor
            com.intuit.biddingSystem.user.model.User vendor = slot.getProduct().getVendor().getUser();
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