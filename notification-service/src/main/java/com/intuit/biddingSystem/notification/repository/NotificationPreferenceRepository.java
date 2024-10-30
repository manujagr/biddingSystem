package com.intuit.biddingSystem.notification.repository;

import com.intuit.biddingSystem.notification.NotificationChannel;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import com.intuit.biddingSystem.notification.NotificationPreference;
import com.intuit.biddingSystem.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationPreferenceRepository extends JpaRepository<com.intuit.biddingSystem.notification.NotificationPreference, UUID> {

    // Used in NotificationStrategyFactory
    List<NotificationPreference> findByUserAndSubscribedTrue(User user);


    @Query("select np.user from NotificationPreference np WHERE np.user IN :users AND np.messageType = :messageType AND np.subscribed = true")
    List<User> findBiddersWhoAreSubscribedForMessageType(@Param("users") List<User> users, @Param("messageType") MessageType messageType);

    @Query("select np.user from NotificationPreference np where np.messageType= :messageType and np.subscribed=true")
    List<User> findUserByMessageTypeAndSubscribed(MessageType messageType);

    // Used in EndAuctionJob
    boolean existsByUserAndMessageTypeAndSubscribedTrue(User user, MessageType messageType);

    // Used in NotificationService
    boolean existsByUserAndChannelAndMessageTypeAndSubscribedTrue(User user, NotificationChannel channel, MessageType messageType);
}
