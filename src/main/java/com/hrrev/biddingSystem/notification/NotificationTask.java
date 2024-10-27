package com.hrrev.biddingSystem.notification;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class NotificationTask {
    private UUID userId;
    private NotificationMessage message;
    private NotificationChannel channel;

    // Constructors

    public NotificationTask(UUID userId, NotificationMessage message, NotificationChannel channel) {
        this.userId = userId;
        this.message = message;
        this.channel = channel;
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public NotificationMessage getMessage() { return message; }
    public void setMessage(NotificationMessage message) { this.message = message; }

    public NotificationChannel getChannel() { return channel; }
    public void setChannel(NotificationChannel channel) { this.channel = channel; }
}
