package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationChannel;
import com.hrrev.biddingSystem.notification.NotificationTask;
import com.hrrev.biddingSystem.notification.NotificationStrategy;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class NotificationTaskConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTaskConsumer.class);

    private final Map<String, NotificationStrategy> notificationStrategies;
    private final UserRepository userRepository;

    @Autowired
    public NotificationTaskConsumer(Map<String, NotificationStrategy> notificationStrategies,
                                    UserRepository userRepository) {
        this.notificationStrategies = notificationStrategies;
        this.userRepository = userRepository;
    }

    /**
     * Consumes NotificationTask messages from the notification-tasks Kafka topic and processes them.
     *
     * @param task The NotificationTask message.
     */
    @KafkaListener(topics = "notification-tasks", groupId = "notification-task-group",
            containerFactory = "notificationTaskKafkaListenerContainerFactory")
    public void consume(NotificationTask task) {
        logger.info("Processing NotificationTask for user ID: {}", task.getUserId());

        try {
            // Fetch the user or throw if not found
            User user = userRepository.findById(task.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found for ID: " + task.getUserId()));

            // Get the notification strategy based on channel
            NotificationStrategy strategy = getStrategy(task.getChannel());

            if (strategy != null) {
                strategy.sendNotification(user, task.getMessage());
                logger.info("Notification sent to user ID: {} via channel: {}", user.getUserId(), task.getChannel());
            } else {
                logger.error("No NotificationStrategy found for channel: {}", task.getChannel());
            }

        } catch (NoSuchElementException ex) {
            // Log error for missing user
            logger.error("User not found: {}", ex.getMessage());
            // Optionally, add handling for missing users, like logging to a dead-letter queue
        } catch (Exception ex) {
            logger.error("Error processing NotificationTask for user ID: {}: {}", task.getUserId(), ex.getMessage(), ex);
            // Optional handling for unexpected errors, e.g., retries or dead-letter queue
        }
    }

    /**
     * Retrieves the appropriate NotificationStrategy based on the notification channel.
     *
     * @param channel The notification channel.
     * @return The corresponding NotificationStrategy, or null if none found.
     */
    private NotificationStrategy getStrategy(NotificationChannel channel) {
        String strategyBeanName = channel.name().toLowerCase() + "NotificationStrategy";
        return notificationStrategies.get(strategyBeanName);
    }
}