package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("notificationExecutor")
    public void notifyAuctionStarted(List<User> users, AuctionSlot slot) {
        users.forEach(user -> sendAuctionStartedEmail(user, slot));
    }

    @Async("notificationExecutor")
    public void notifyAuctionEnded(Set<User> users, AuctionSlot slot, UUID winnerUserId) {
        users.forEach(user -> {
            if (user.getUserId().equals(winnerUserId)) {
                sendWinnerEmail(user, slot);
            } else {
                sendAuctionEndedEmail(user, slot);
            }
        });
        User vendor = slot.getProduct().getVendor().getUser();
        sendVendorNotification(vendor, slot, winnerUserId);
    }

    private void sendVendorNotification(User vendor, AuctionSlot slot, UUID winnerUserId) {
        // Build and send email to vendor
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(vendor.getEmail());
        message.setSubject("Your Auction Has Ended");
        message.setText("Your auction for " + slot.getProduct().getName() + " has ended.");
        mailSender.send(message);
    }

    private void sendAuctionStartedEmail(User user, AuctionSlot slot) {
        // Build and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Auction Started");
        message.setText("The auction for " + slot.getProduct().getName() + " has started.");
        mailSender.send(message);
    }

    private void sendAuctionEndedEmail(User user, AuctionSlot slot) {
        // Build and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Auction Ended");
        message.setText("The auction for " + slot.getProduct().getName() + " has ended.");
        mailSender.send(message);
    }

    private void sendWinnerEmail(User user, AuctionSlot slot) {
        // Build and send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Congratulations! You Won the Auction");
        message.setText("You have won the auction for " + slot.getProduct().getName() + "!");
        mailSender.send(message);
    }
}
