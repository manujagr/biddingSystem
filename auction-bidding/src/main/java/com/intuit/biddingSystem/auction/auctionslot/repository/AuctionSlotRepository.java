package com.intuit.biddingSystem.auction.auctionslot.repository;

import com.intuit.biddingSystem.auction.auctionslot.model.AuctionSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionSlotRepository extends JpaRepository<AuctionSlot, UUID> {
    List<AuctionSlot> findByStatus(AuctionSlot.SlotStatus status);
    List<AuctionSlot> findByProductAndStatus(com.intuit.biddingSystem.product.model.Product product, AuctionSlot.SlotStatus status);
}

