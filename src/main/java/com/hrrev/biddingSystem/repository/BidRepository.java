package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findBySlotOrderByBidAmountDesc(AuctionSlot slot);
    Optional<Bid> findTopBySlotOrderByBidAmountDesc(AuctionSlot slot);
}


