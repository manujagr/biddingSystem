package com.hrrev.biddingSystem.notification;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationConsumer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationStrategyFactory notificationStrategyFactory;

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(UserNotification userNotification) {
        User user = userRepository.findById(userNotification.getUserId()).orElse(null);
        if (user != null) {
            NotificationMessage message = userNotification.getMessage();

            // Get the strategies for the user and message type
            List<NotificationStrategy> strategies = notificationStrategyFactory.getStrategies(user, message.getType());

            for (NotificationStrategy strategy : strategies) {
                strategy.sendNotification(user, message);
            }
        }
    }
}
