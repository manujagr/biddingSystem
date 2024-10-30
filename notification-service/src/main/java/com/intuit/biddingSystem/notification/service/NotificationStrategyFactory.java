package com.intuit.biddingSystem.notification.service;

import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.notification.NotificationPreference;
import com.intuit.biddingSystem.notification.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NotificationStrategyFactory {

    private NotificationPreferenceRepository preferenceRepository;

    private Map<String, NotificationStrategy> notificationStrategies;

    @Autowired
    public NotificationStrategyFactory(NotificationPreferenceRepository notificationPreferenceRepository, Map<String, NotificationStrategy> notificationStrategies){
        this.notificationStrategies = notificationStrategies;
        this.preferenceRepository = notificationPreferenceRepository;
    }

    public List<NotificationStrategy> getStrategies(com.intuit.biddingSystem.user.model.User user, MessageType messageType) {
        List<NotificationStrategy> strategies = new ArrayList<>();

        List<NotificationPreference> preferences = preferenceRepository.findByUserAndSubscribedTrue(user);

        for (NotificationPreference preference : preferences) {
            if (preference.getMessageType() == messageType) {
                String strategyBeanName = preference.getChannel().name().toLowerCase() + "NotificationStrategy";
                NotificationStrategy strategy = notificationStrategies.get(strategyBeanName);
                if (strategy != null) {
                    strategies.add(strategy);
                }
            }
        }

        return strategies;
    }
}
