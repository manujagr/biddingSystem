package com.hrrev.biddingSystem.notification;

import com.hrrev.biddingSystem.model.NotificationPreference;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import com.hrrev.biddingSystem.repository.NotificationPreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class NotificationStrategyFactory {

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;

    @Autowired
    private Map<String, NotificationStrategy> notificationStrategies;

    public List<NotificationStrategy> getStrategies(User user, MessageType messageType) {
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
