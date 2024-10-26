package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationChannel;
import com.hrrev.biddingSystem.notification.NotificationStrategy;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import com.hrrev.biddingSystem.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
public class NotificationService {

    private NotificationPreferenceRepository preferenceRepository;
    private Map<String, NotificationStrategy> notificationStrategies;

    @Autowired
    public NotificationService(NotificationPreferenceRepository notificationPreferenceRepository, Map<String, NotificationStrategy> notificationStrategies){
        this.notificationStrategies = notificationStrategies;
        this.preferenceRepository = notificationPreferenceRepository;
    }

    @Async("notificationExecutor")
    public void notifyUsers(Set<User> users, NotificationMessage message) {
        for (User user : users) {
            notifyUser(user, message);
        }
    }

    @Async("notificationExecutor")
    public void notifyUser(User user, NotificationMessage message) {
        MessageType messageType = message.getType();

        for (NotificationChannel channel : NotificationChannel.values()) {
            if (isUserSubscribed(user, channel, messageType)) {
                String strategyBeanName = channel.name().toLowerCase() + "NotificationStrategy";
                NotificationStrategy strategy = notificationStrategies.get(strategyBeanName);
                if (strategy != null) {
                    strategy.sendNotification(user, message);
                }
            }
        }
    }

    private boolean isUserSubscribed(User user, NotificationChannel channel, MessageType messageType) {
        return preferenceRepository.existsByUserAndChannelAndMessageTypeAndSubscribedTrue(user, channel, messageType);
    }
}
