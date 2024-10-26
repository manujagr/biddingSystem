package com.hrrev.biddingSystem.notification;

import com.hrrev.biddingSystem.model.User;

public interface NotificationStrategy {
    void sendNotification(User user, NotificationMessage message);
}