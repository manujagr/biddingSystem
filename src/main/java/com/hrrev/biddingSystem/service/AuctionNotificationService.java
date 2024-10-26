package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.notification.NotificationMessage.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class AuctionNotificationService {

    private NotificationService notificationService;

    @Autowired
    public AuctionNotificationService(NotificationService notificationService){
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
        notificationService.notifyUser(user, message);
    }

    private void sendAuctionEndedNotification(User user, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.AUCTION_ENDED,
                "Auction Ended",
                "The auction for " + slot.getProduct().getName() + " has ended."
        );
        notificationService.notifyUser(user, message);
    }

    private void sendWinnerNotification(User user, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.WINNER_NOTIFICATION,
                "Congratulations! You Won the Auction",
                "You have won the auction for " + slot.getProduct().getName() + "!"
        );
        notificationService.notifyUser(user, message);
    }

    private void sendVendorAuctionStartedNotification(User vendor, AuctionSlot slot) {
        NotificationMessage message = new NotificationMessage(
                MessageType.VENDOR_NOTIFICATION,
                "Your Auction Has Started",
                "Your auction for " + slot.getProduct().getName() + " has started."
        );
        notificationService.notifyUser(vendor, message);
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
        notificationService.notifyUser(vendor, message);
    }
}
