package com.intuit.biddingSystem.notification.service;


import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.user.model.User;

public interface NotificationStrategy {
    void sendNotification(User user, NotificationMessage message);
}