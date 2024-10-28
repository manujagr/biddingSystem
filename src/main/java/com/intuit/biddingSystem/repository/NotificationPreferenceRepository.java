package com.intuit.biddingSystem.repository;

import com.intuit.biddingSystem.model.NotificationPreference;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.notification.NotificationChannel;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    // Used in NotificationStrategyFactory
    List<NotificationPreference> findByUserAndSubscribedTrue(User user);

    // Used in EndAuctionJob
    boolean existsByUserAndMessageTypeAndSubscribedTrue(User user, MessageType messageType);

    // Used in NotificationService
    boolean existsByUserAndChannelAndMessageTypeAndSubscribedTrue(User user, NotificationChannel channel, MessageType messageType);
}
