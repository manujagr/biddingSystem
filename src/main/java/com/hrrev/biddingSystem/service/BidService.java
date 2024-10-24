package com.hrrev.biddingSystem.service;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.User;
import com.hrrev.biddingSystem.repository.AuctionSlotRepository;
import com.hrrev.biddingSystem.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private AuctionSlotRepository auctionSlotRepository;

    public Bid placeBid(Long slotId, Long userId, BigDecimal bidAmount) throws Exception {
        AuctionSlot slot = auctionSlotRepository.findById(slotId)
                .orElseThrow(() -> new Exception("Auction slot not found"));

        if (slot.getStatus() != AuctionSlot.SlotStatus.ACTIVE) {
            throw new Exception("Auction slot is not active");
        }

        BigDecimal basePrice = slot.getProduct().getBasePrice();

        Optional<Bid> highestBidOpt = bidRepository.findTopBySlotOrderByBidAmountDesc(slot);
        BigDecimal highestBidAmount = highestBidOpt.map(Bid::getBidAmount).orElse(basePrice);

        if (bidAmount.compareTo(highestBidAmount) <= 0) {
            throw new Exception("Bid amount must be higher than the current highest bid");
        }

        Bid bid = new Bid();
        bid.setSlot(slot);
        bid.setUser(new User(userId)); // Create a User object with the given ID
        bid.setBidAmount(bidAmount);
        bid.setBidTime(LocalDateTime.now());

        return bidRepository.save(bid);
    }

    // Additional methods
}

