package com.hrrev.biddingSystem.consumers;

import com.hrrev.biddingSystem.events.AuctionEndedEvent;
import com.hrrev.biddingSystem.exception.ResourceNotFoundException;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.notification.NotificationMessage;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AuctionEndedEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(AuctionEndedEventConsumer.class);

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "auction-ended", groupId = "notification-group")
    public void consume(AuctionEndedEvent event) {
        logger.info("Received AuctionEndedEvent for slot ID: {}", event.getSlotId());

        try {
            // Fetch the AuctionSlot
            AuctionSlot slot = auctionSlotRepository.findById(event.getSlotId())
                    .orElseThrow(() -> {
                        logger.error("AuctionSlot not found for ID: {}", event.getSlotId());
                        return new ResourceNotFoundException("AuctionSlot not found for ID: " + event.getSlotId(), "AUCTION_404");
                    });

            logger.info("Processing AuctionEndedEvent for slot: {}", slot.getSlotId());

            // Fetch bids
            List<Bid> bids = bidRepository.findBySlot(slot);
            logger.debug("Found {} bids for slot: {}", bids.size(), slot.getSlotId());

            if (bids.isEmpty()) {
                logger.warn("No bids found for AuctionSlot ID: {}", slot.getSlotId());
                // Optionally, handle this scenario as needed
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

            // Notify bidders and vendor
            notificationService.notifyUsers(bidders, message);
            logger.info("Notification process initiated for AuctionEndedEvent on slot: {}", slot.getSlotId());

        } catch (ResourceNotFoundException ex) {
            // Custom exception already logged in the lambda
            // Optionally, handle specific actions for not found resources
        } catch (Exception ex) {
            logger.error("An unexpected error occurred while processing AuctionEndedEvent: {}", ex.getMessage(), ex);
            // Optionally, rethrow or handle the exception to prevent message loss
            // For example, send to a dead-letter topic or alert monitoring systems
        }
    }
}