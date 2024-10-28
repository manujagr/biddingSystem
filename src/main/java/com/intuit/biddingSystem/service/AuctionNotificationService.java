package com.intuit.biddingSystem.service;

import com.intuit.biddingSystem.model.AuctionSlot;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.notification.NotificationMessage;
import com.intuit.biddingSystem.notification.NotificationMessage.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AuctionNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(AuctionNotificationService.class);
    private final NotificationService notificationService;

    public AuctionNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void notifyAuctionStarted(Set<User> users, AuctionSlot slot) {
        users.forEach(user -> sendAuctionStartedNotification(user, slot));
    }

    public void notifyVendorAuctionStarted(User vendor, AuctionSlot slot) {
        sendVendorAuctionStartedNotification(vendor, slot);
    }

    public void notifyAuctionEnded(Set<User> users, AuctionSlot slot, UUID winnerUserId) {
        users.forEach(user -> {
            if (user.getUserId().equals(winnerUserId)) {
                sendWinnerNotification(user, slot);
            } else {
                sendAuctionEndedNotification(user, slot);
            }
        });
        User vendor = slot.getProduct().getVendor().getUser();
        sendVendorAuctionEndedNotification(vendor, slot, winnerUserId);
    }

    private void sendAuctionStartedNotification(User user, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.AUCTION_STARTED,
                "Auction Started",
                "The auction for " + slot.getProduct().getName() + " has started."
        );
        try {
            notificationService.notifyUser(user, message);
            logger.info("Auction started notification sent to user ID: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send auction started notification to user ID: {}: {}", user.getUserId(), e.getMessage());
        }
    }

    private void sendAuctionEndedNotification(User user, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.AUCTION_ENDED,
                "Auction Ended",
                "The auction for " + slot.getProduct().getName() + " has ended."
        );
        try {
            notificationService.notifyUser(user, message);
            logger.info("Auction ended notification sent to user ID: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send auction ended notification to user ID: {}: {}", user.getUserId(), e.getMessage());
        }
    }

    private void sendWinnerNotification(User user, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.WINNER_NOTIFICATION,
                "Congratulations! You Won the Auction",
                "You have won the auction for " + slot.getProduct().getName() + "!"
        );
        try {
            notificationService.notifyUser(user, message);
            logger.info("Winner notification sent to user ID: {}", user.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send winner notification to user ID: {}: {}", user.getUserId(), e.getMessage());
        }
    }

    private void sendVendorAuctionStartedNotification(User vendor, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.VENDOR_NOTIFICATION,
                "Your Auction Has Started",
                "Your auction for " + slot.getProduct().getName() + " has started."
        );
        try {
            notificationService.notifyUser(vendor, message);
            logger.info("Vendor auction started notification sent to vendor ID: {}", vendor.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send auction started notification to vendor ID: {}: {}", vendor.getUserId(), e.getMessage());
        }
    }

    private void sendVendorAuctionEndedNotification(User vendor, AuctionSlot slot, UUID winnerUserId) {
        String content = "Your auction for " + slot.getProduct().getName() + " has ended.";
        if (winnerUserId != null) {
            content += " The winning bidder's user ID is " + winnerUserId + ".";
        } else {
            content += " There were no bids on your auction.";
        }
        NotificationMessage message = new NotificationMessage(
                MessageType.VENDOR_NOTIFICATION,
                "Your Auction Has Ended",
                content
        );
        try {
            notificationService.notifyUser(vendor, message);
            logger.info("Vendor auction ended notification sent to vendor ID: {}", vendor.getUserId());
        } catch (Exception e) {
            logger.error("Failed to send auction ended notification to vendor ID: {}: {}", vendor.getUserId(), e.getMessage());
        }
    }
}