package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Fetch users who have subscribed to a specific category
    @Query("SELECT DISTINCT np.user FROM NotificationPreference np WHERE np.category = :category AND np.subscribed = true")
    List<User> findUsersSubscribedToCategory(@Param("category") Category category);

    // Fetch users who have bid on a specific auction slot
    @Query("SELECT DISTINCT b.user FROM Bid b WHERE b.slot = :slot")
    List<User> findUsersWhoBidOnSlot(@Param("slot") AuctionSlot slot);

    // Fetch users who have bid on a slot and are subscribed to a specific message type
    @Query("SELECT DISTINCT b.user FROM Bid b JOIN NotificationPreference np ON b.user.userId = np.user.userId WHERE b.slot = :slot AND np.messageType = :messageType AND np.subscribed = true")
    List<User> findUsersWhoBidOnSlotAndSubscribed(
            @Param("slot") AuctionSlot slot,
            @Param("messageType") NotificationMessage.MessageType messageType
    );

    // Fetch vendor of a product
    @Query("SELECT p.vendor.user FROM Product p WHERE p.productId = :productId")
    User findVendorByProductId(@Param("productId") UUID productId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
