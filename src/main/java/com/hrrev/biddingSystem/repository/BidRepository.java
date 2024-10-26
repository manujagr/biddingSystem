package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Bid;
import com.hrrev.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findBySlot(AuctionSlot slot);
    Optional<Bid> findTopBySlotOrderByBidAmountDesc(AuctionSlot slot);
    @Query("SELECT b FROM Bid b WHERE b.slot.product = :product")
    Set<Bid> findByProduct(Product product);
}


