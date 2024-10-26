// File: src/main/java/com/hrrev/biddingSystem/model/NotificationPreference.java

package com.hrrev.biddingSystem.model;

import com.hrrev.biddingSystem.notification.NotificationChannel;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "channel", "message_type"})})
public class NotificationPreference {

    @Id
    @GeneratedValue
    private UUID preferenceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;

    @Column(nullable = false)
    private boolean subscribed;

    // Constructors
    public NotificationPreference() {}

    public NotificationPreference(User user, NotificationChannel channel, MessageType messageType, boolean subscribed) {
        this.user = user;
        this.channel = channel;
        this.messageType = messageType;
        this.subscribed = subscribed;
    }

    // Getters and Setters
    public UUID getPreferenceId() {
        return preferenceId;
    }

    public User getUser() {
        return user;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
