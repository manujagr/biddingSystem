package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.model.Winner;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.WinnerRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AuctionEndedEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuctionEndedEventConsumer.class);

    private AuctionSlotRepository auctionSlotRepository;

    private BidRepository bidRepository;

    private NotificationService notificationService;

    private WinnerRepository winnerRepository;

    @Autowired
    public AuctionEndedEventConsumer(AuctionSlotRepository auctionSlotRepository, BidRepository bidRepository, NotificationService notificationService, WinnerRepository winnerRepository){
        this.auctionSlotRepository = auctionSlotRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
        this.winnerRepository = winnerRepository;
    }

    @KafkaListener(topics = "auction-ended", groupId = "notification-group")
    public void consume(AuctionEndedEvent event) {
        logger.info("Received AuctionEndedEvent for slot ID: {}", event.getSlotId());

        try {
            // Fetch the AuctionSlot or throw if not found
            AuctionSlot slot = auctionSlotRepository.findById(event.getSlotId())
                    .orElseThrow(() -> new NoSuchElementException("AuctionSlot not found for ID: " + event.getSlotId()));

            logger.info("Processing AuctionEndedEvent for slot ID: {}", slot.getSlotId());

            // Fetch bids for the AuctionSlot
            List<Bid> bids = bidRepository.findBySlot(slot);
            logger.debug("Found {} bids for slot ID: {}", bids.size(), slot.getSlotId());

            if (bids.isEmpty()) {
                logger.warn("No bids found for AuctionSlot ID: {}", slot.getSlotId());
                // Notify the vendor about no bids
                User vendor = slot.getProduct().getVendor().getUser();
                NotificationMessage vendorMessage = new NotificationMessage(
                        NotificationMessage.MessageType.VENDOR_NOTIFICATION,
                        "Auction Ended Without Bids",
                        "Your auction for " + slot.getProduct().getName() + " ended with no bids."
                );
                notificationService.notifyUser(vendor, vendorMessage);
                return;
            }

            // Find the highest bid
            //We can use Redis Sorted Sets
            Bid winningBid = bids.stream()
                    .max(Comparator.comparing(Bid::getBidAmount))
                    .orElse(null);

            if (winningBid == null) {
                logger.error("Unable to determine the winning bid for slot ID: {}", slot.getSlotId());
                return;
            }

            logger.info("Winning bid found: {} by user ID: {}", winningBid.getBidAmount(), winningBid.getUser().getUserId());

            // Check if a Winner already exists for this slot
            Optional<Winner> existingWinner = winnerRepository.findBySlot(slot);

            Winner winner;
            if (existingWinner.isPresent()) {
                logger.info("Winner already exists for slot ID: {}", slot.getSlotId());
                winner = existingWinner.get();
            } else {
                // Create and save a new Winner entity
                winner = new Winner();
                winner.setSlot(slot);
                winner.setUser(winningBid.getUser());
                winner.setBid(winningBid);
                winner.setNotified(false); // Set to true after notification is sent

                winnerRepository.save(winner);
                logger.info("Winner saved for slot ID: {}", slot.getSlotId());
            }

            // Create a notification message for the winner
            NotificationMessage winnerMessage = new NotificationMessage(
                    NotificationMessage.MessageType.WINNER_NOTIFICATION,
                    "Congratulations! You Won the Auction",
                    "You have won the auction for " + slot.getProduct().getName() + " with a bid of " + winningBid.getBidAmount() + "."
            );

            // Notify the winner
            notificationService.notifyUser(winningBid.getUser(), winnerMessage);
            logger.info("Winner notified: User ID {}", winningBid.getUser().getUserId());

            // Update the Winner entity to indicate notification has been sent
            winner.setNotified(true);
            winner.setNotificationTime(LocalDateTime.now());
            winnerRepository.save(winner);

            // Notify other bidders that they did not win
            Set<User> losingBidders = bids.stream()
                    .map(Bid::getUser)
                    .filter(user -> !user.getUserId().equals(winningBid.getUser().getUserId()))
                    .collect(Collectors.toSet());

            if (!losingBidders.isEmpty()) {
                NotificationMessage losingBidderMessage = new NotificationMessage(
                        NotificationMessage.MessageType.AUCTION_ENDED,
                        "Auction Ended",
                        "The auction for " + slot.getProduct().getName() + " has ended. Unfortunately, you did not win."
                );
                notificationService.notifyUsers(losingBidders, losingBidderMessage);
                logger.info("Notified {} losing bidders for slot ID: {}", losingBidders.size(), slot.getSlotId());
            }

            // Notify the vendor about the auction result
            User vendor = slot.getProduct().getVendor().getUser();
            NotificationMessage vendorMessage = new NotificationMessage(
                    NotificationMessage.MessageType.VENDOR_NOTIFICATION,
                    "Your Auction Has Ended",
                    "Your auction for " + slot.getProduct().getName() + " has ended. The winning bid was " + winningBid.getBidAmount() + "."
            );
            notificationService.notifyUser(vendor, vendorMessage);
            logger.info("Vendor notified about auction result: User ID {}", vendor.getUserId());

        } catch (NoSuchElementException ex) {
            // Log error for missing AuctionSlot
            logger.error("AuctionSlot not found: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while processing AuctionEndedEvent for slot ID: {}: {}", event.getSlotId(), ex.getMessage(), ex);
            // Optional: handle message loss by sending to a dead-letter topic or alerting a monitoring system
        }
    }
}
