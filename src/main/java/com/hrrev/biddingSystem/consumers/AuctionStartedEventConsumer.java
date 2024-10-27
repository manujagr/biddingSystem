package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionStartedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.Product;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuctionStartedEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuctionStartedEventConsumer.class);

    private final AuctionSlotRepository auctionSlotRepository;
    private final BidRepository bidRepository;
    private final NotificationService notificationService;

    @Autowired
    public AuctionStartedEventConsumer(AuctionSlotRepository auctionSlotRepository, BidRepository bidRepository, NotificationService notificationService) {
        this.auctionSlotRepository = auctionSlotRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "auction-started", groupId = "notification-group")
    public void consume(AuctionStartedEvent event) {
        logger.info("Received AuctionStartedEvent for slot ID: {}", event.getSlotId());

        try {
            // Fetch the AuctionSlot or throw if not found
            AuctionSlot slot = auctionSlotRepository.findById(event.getSlotId())
                    .orElseThrow(() -> new NoSuchElementException("AuctionSlot not found for ID: " + event.getSlotId()));

            logger.info("Processing AuctionStartedEvent for slot ID: {}", slot.getSlotId());

            Product product = slot.getProduct();
            logger.debug("Auction started for product: {}", product.getName());

            // Fetch all bids for previous auction slots of this product
            Set<Bid> bids = bidRepository.findByProduct(product);
            logger.debug("Found {} bids for product: {}", bids.size(), product.getName());

            if (bids.isEmpty()) {
                logger.warn("No bids found for product: {}", product.getName());
                // Optionally, handle this scenario as needed (e.g., notify vendor)
            }

            // Extract unique users from bids
            Set<User> users = bids.stream()
                    .map(Bid::getUser)
                    .collect(Collectors.toSet());
            logger.debug("Unique users to notify: {}", users.size());

            // Create NotificationMessage for auction started
            NotificationMessage message = new NotificationMessage(
                    NotificationMessage.MessageType.AUCTION_STARTED,
                    "Auction Started",
                    "A new auction for " + product.getName() + " has started."
            );

            // Notify users
            notificationService.notifyUsers(users, message);
            logger.info("Notification process initiated for AuctionStartedEvent on product: {}", product.getName());

            // Optionally, notify the vendor separately
            User vendor = product.getVendor().getUser();
            if (vendor != null) {
                NotificationMessage vendorMessage = new NotificationMessage(
                        NotificationMessage.MessageType.VENDOR_NOTIFICATION,
                        "Your Auction Has Started",
                        "Your auction for " + product.getName() + " has started."
                );
                notificationService.notifyUser(vendor, vendorMessage);
                logger.info("Vendor {} notified about the auction start for product: {}", vendor.getUsername(), product.getName());
            } else {
                logger.warn("Vendor not found for product: {}", product.getName());
            }

        } catch (NoSuchElementException ex) {
            // Log error for missing AuctionSlot
            logger.error("AuctionSlot not found: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while processing AuctionStartedEvent: {}", ex.getMessage(), ex);
            // Optional: handle message loss by sending to a dead-letter topic or alerting a monitoring system
        }
    }
}