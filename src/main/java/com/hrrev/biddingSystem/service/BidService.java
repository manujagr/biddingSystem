package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.dto.BidRegistrationRequest;
import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import com.hrrev.biddingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BidService {


    private final BidRepository bidRepository;
    private final AuctionSlotRepository auctionSlotRepository;
    private final UserRepository userRepository;

    @Autowired
    public BidService(BidRepository bidRepository, AuctionSlotRepository auctionSlotRepository, UserRepository userRepository) {
        this.bidRepository = bidRepository;
        this.auctionSlotRepository = auctionSlotRepository;
        this.userRepository = userRepository;
    }

    public Bid placeBid(BidRegistrationRequest bidRequest, Long userId) throws Exception {
        AuctionSlot slot = auctionSlotRepository.findById(bidRequest.getSlotId())
                .orElseThrow(() -> new Exception("Auction slot not found"));


        if (slot.getStatus() != AuctionSlot.SlotStatus.ACTIVE) {
            throw new Exception("Auction slot is not active");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Get base price
        BigDecimal basePrice = slot.getProduct().getBasePrice();

        Optional<Bid> highestBidOpt = bidRepository.findTopBySlotOrderByBidAmountDesc(slot);
        BigDecimal highestBidAmount = highestBidOpt.map(Bid::getBidAmount).orElse(basePrice);

        if (bidRequest.getBidAmount().compareTo(highestBidAmount) <= 0) {
            throw new Exception("Bid amount must be higher than the current highest bid");
        }

        Bid bid = new Bid();
        bid.setSlot(slot);
        bid.setUser(user);
        bid.setBidAmount(bidRequest.getBidAmount());
        bid.setBidTime(LocalDateTime.now());

        return bidRepository.save(bid);
    }

    // Additional methods
}

