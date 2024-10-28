package com.intuit.biddingSystem.notification;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
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
}
