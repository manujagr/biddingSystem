package com.intuit.biddingSystem.notification;

import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "notification_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "channel", "message_type"}),
        indexes = {
                @Index(name = "idx_notification_preference_user_id", columnList = "user_id")
        }
)
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = false)
//    private Category category;

    @Column(nullable = false)
    private boolean subscribed;
}
