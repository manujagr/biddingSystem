package com.intuit.biddingSystem.notification;

import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationConsumer {

    private UserRepository userRepository;

    private NotificationStrategyFactory notificationStrategyFactory;

    @Autowired
    public NotificationConsumer(UserRepository userRepository, NotificationStrategyFactory notificationStrategyFactory){
        this.notificationStrategyFactory = notificationStrategyFactory;
        this.userRepository = userRepository;
    }

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
