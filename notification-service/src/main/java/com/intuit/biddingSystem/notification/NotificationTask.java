package com.intuit.biddingSystem.notification;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Data
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
}
