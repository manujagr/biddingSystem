package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.BidRegistrationRequest;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BidService {

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;
    private final AuctionSlotRepository auctionSlotRepository;
    private final UserRepository userRepository;

    public BidService(BidRepository bidRepository, AuctionSlotRepository auctionSlotRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionSlotRepository = auctionSlotRepository;
        this.userRepository = userRepository;
    }

    public Bid placeBid(BidRegistrationRequest bidRequest, UUID userId) {
        logger.info("Placing bid for user ID: {} on auction slot ID: {}", userId, bidRequest.getSlotId());

        AuctionSlot slot = auctionSlotRepository.findById(bidRequest.getSlotId())
                .orElseThrow(() -> {
                    logger.error("Auction slot not found for ID: {}", bidRequest.getSlotId());
                    return new NoSuchElementException("Auction slot not found");
                });

        if (slot.getStatus() != AuctionSlot.SlotStatus.ACTIVE) {
            logger.warn("Attempt to place bid on inactive auction slot ID: {}", bidRequest.getSlotId());
            throw new IllegalArgumentException("Auction slot is not active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for ID: {}", userId);
                    return new NoSuchElementException("User not found");
                });

        // Get base price
        BigDecimal basePrice = slot.getProduct().getBasePrice();

        if (bidRequest.getBidAmount().compareTo(basePrice) <= 0) {
            logger.warn("Bid amount {} is not higher than the base price {} for product {}", bidRequest.getBidAmount(), basePrice, slot.getProduct().getName());
            throw new IllegalArgumentException("Bid amount must be higher than the current base price");
        }

        // Create and save the new bid
        Bid bid = new Bid();
        bid.setSlot(slot);
        bid.setUser(user);
        bid.setBidAmount(bidRequest.getBidAmount());
        bid.setBidTime(LocalDateTime.now());

        Bid savedBid = bidRepository.save(bid);
        logger.info("Bid successfully placed with ID: {} for auction slot ID: {}", savedBid.getBidId(), slot.getSlotId());

        return savedBid;
    }
}