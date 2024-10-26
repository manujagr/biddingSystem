package com.hrrev.biddingSystem.notification;

public class NotificationMessage {

    public enum MessageType {
        AUCTION_STARTED,
        AUCTION_ENDED,
        WINNER_NOTIFICATION,
        VENDOR_NOTIFICATION,
    }

    private MessageType type;
    private String subject;
    private String content;

    public NotificationMessage(MessageType type, String subject, String content) {
        this.type = type;
        this.subject = subject;
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
