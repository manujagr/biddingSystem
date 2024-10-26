package com.hrrev.biddingSystem.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UserNotification {

    private UUID userId;
    private NotificationMessage message;

    @JsonCreator
    public UserNotification(
            @JsonProperty("userId") UUID userId,
            @JsonProperty("message") NotificationMessage message) {
        this.userId = userId;
        this.message = message;
    }

    public UUID getUserId() {
        return userId;
    }

    public NotificationMessage getMessage() {
        return message;
    }

    // Optionally, add setters if needed
}
