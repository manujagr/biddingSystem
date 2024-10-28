package com.intuit.biddingSystem.service;

import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.notification.NotificationChannel;
import com.intuit.biddingSystem.notification.NotificationStrategy;
import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.notification.NotificationTask;
import com.intuit.biddingSystem.repository.NotificationPreferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final String NOTIFICATION_TASKS_TOPIC = "notification-tasks";

    private final NotificationPreferenceRepository preferenceRepository;
    private final Map<String, NotificationStrategy> notificationStrategies;
    private final KafkaTemplate<String, NotificationTask> kafkaTemplate;

    public NotificationService(NotificationPreferenceRepository notificationPreferenceRepository,
                               Map<String, NotificationStrategy> notificationStrategies,
                               KafkaTemplate<String, NotificationTask> kafkaTemplate) {
        this.preferenceRepository = notificationPreferenceRepository;
        this.notificationStrategies = notificationStrategies;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async("notificationExecutor")
    public void notifyUsers(Set<User> users, NotificationMessage message) {
        users.forEach(user -> notifyUser(user, message));
    }

    @Async("notificationExecutor")
    public void notifyUser(User user, NotificationMessage message) {
        MessageType messageType = message.getType();
        for (NotificationChannel channel : NotificationChannel.values()) {
            if (isUserSubscribed(user, channel, messageType)) {
                try {
                    NotificationTask task = new NotificationTask(user.getUserId(), message, channel);
                    kafkaTemplate.send(NOTIFICATION_TASKS_TOPIC, user.getUserId().toString(), task);
                    logger.info("Notification task for user {} on channel {} sent to Kafka.", user.getUserId(), channel);
                } catch (Exception e) {
                    logger.error("Failed to send notification task for user {} on channel {}: {}", user.getUserId(), channel, e.getMessage(), e);
                }
            } else {
                logger.debug("User {} not subscribed to channel {} for message type {}", user.getUserId(), channel, messageType);
            }
        }
    }

    private boolean isUserSubscribed(User user, NotificationChannel channel, MessageType messageType) {
        boolean isSubscribed = preferenceRepository.existsByUserAndChannelAndMessageTypeAndSubscribedTrue(user, channel, messageType);
        logger.debug("User {} subscription status for channel {} and message type {}: {}", user.getUserId(), channel, messageType, isSubscribed);
        return isSubscribed;
    }
}
