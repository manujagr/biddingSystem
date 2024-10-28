package com.intuit.biddingSystem.notification;

import com.intuit.biddingSystem.model.User;

public interface NotificationStrategy {
    void sendNotification(User user, NotificationMessage message);
}