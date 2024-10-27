package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuctionEndedEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuctionEndedEventConsumer.class);

    private AuctionSlotRepository auctionSlotRepository;

    private BidRepository bidRepository;

    private NotificationService notificationService;

    @Autowired
    public AuctionEndedEventConsumer(AuctionSlotRepository auctionSlotRepository, BidRepository bidRepository, NotificationService notificationService){
        this.auctionSlotRepository = auctionSlotRepository;
        this.bidRepository = bidRepository;
        this.notificationService = notificationService;
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
            }

            // Extract unique users from bids
            Set<User> bidders = bids.stream()
                    .map(Bid::getUser)
                    .collect(Collectors.toSet());
            logger.debug("Unique bidders count: {}", bidders.size());

            // Create NotificationMessage for auction ended
            NotificationMessage message = new NotificationMessage(
                    NotificationMessage.MessageType.AUCTION_ENDED,
                    "Auction Ended",
                    "The auction for " + slot.getProduct().getName() + " has ended."
            );

            // Notify all bidders and the vendor
            notificationService.notifyUsers(bidders, message);
            logger.info("Notification process completed for AuctionEndedEvent on slot ID: {}", slot.getSlotId());

        } catch (NoSuchElementException ex) {
            // Log error for missing AuctionSlot
            logger.error("AuctionSlot not found: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error while processing AuctionEndedEvent for slot ID: {}: {}", event.getSlotId(), ex.getMessage(), ex);
            // Optional: handle message loss by sending to a dead-letter topic or alerting a monitoring system
        }
    }
}
