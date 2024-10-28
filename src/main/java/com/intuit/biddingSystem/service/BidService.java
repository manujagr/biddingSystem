package com.intuit.biddingSystem.service;

import com.intuit.biddingSystem.dto.BidRegistrationRequest;
import com.intuit.biddingSystem.model.AuctionSlot;
import com.intuit.biddingSystem.model.Bid;
import com.intuit.biddingSystem.model.BidMessage;
import com.intuit.biddingSystem.model.User;
import com.intuit.biddingSystem.repository.AuctionSlotRepository;
import com.intuit.biddingSystem.repository.BidRepository;
import com.intuit.biddingSystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BidService {

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);

    private final BidRepository bidRepository;
    private final AuctionSlotRepository auctionSlotRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    private static final String BID_SORTED_SET_KEY = "auction:bids:";
    private static final String AUCTION_DETAILS_KEY = "auction:details:";

    public BidService(BidRepository bidRepository, AuctionSlotRepository auctionSlotRepository, UserRepository userRepository,
                      RedisTemplate<String, Object> redisTemplate) {
        this.bidRepository = bidRepository;
        this.auctionSlotRepository = auctionSlotRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }


    public void placeBid(BidMessage bidMessage) {
        logger.info("Placing bid for user ID: {} on auction slot ID: {}", bidMessage.getUserId(), bidMessage.getAuctionId());
        String auctionKey = AUCTION_DETAILS_KEY + bidMessage.getAuctionId().toString();
        String bidKey = BID_SORTED_SET_KEY + bidMessage.getAuctionId();
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();


        String endTimeStr = (String) redisTemplate.opsForHash().get(auctionKey, "endTime");
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        BigDecimal basePrice = (BigDecimal) redisTemplate.opsForHash().get(auctionKey,"basePrice");

        if(bidMessage.getBidTime().isAfter(endTime)) {
            logger.warn("Attempt to place bid on inactive auction slot ID: {}", bidMessage.getAuctionId());
            throw new IllegalArgumentException("Auction slot is not active");
        }
        if(bidMessage.getAmount().compareTo(basePrice) < 0) {
            logger.warn("Bid amount {} is not higher than the base price {}",
                    bidMessage.getAmount(), basePrice);
            return;
        }

        Bid savedBid = bidRepository.save(getBidFrom(bidMessage));
        logger.info("Bid successfully placed with ID: {} for auction slot ID: {}", savedBid.getBidId(),
                savedBid.getSlot().getSlotId());
        Boolean success = zSet.add(bidKey, savedBid.getBidId(), bidMessage.getAmount().doubleValue());

        if(Boolean.TRUE.equals(success)) {
            logger.info("Bid successfully added to sorted set with ID: {} for auction slot ID: {}", savedBid.getBidId(),
                    savedBid.getSlot().getSlotId());
        }

        return;
    }

    private Bid getBidFrom(BidMessage bidMessage) {
        Bid bid = new Bid();
        User user = userRepository.findById(bidMessage.getUserId()).orElse(null);
        AuctionSlot auctionSlot = auctionSlotRepository.findById(bidMessage.getAuctionId()).orElse(null);
        bid.setUser(user);
        bid.setSlot(auctionSlot);
        bid.setBidTime(bidMessage.getBidTime());
        bid.setBidAmount(bidMessage.getAmount());

        return bid;
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
            throw new IllegalArgumentException("Bid amount must be higher than the current base price "+basePrice);
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