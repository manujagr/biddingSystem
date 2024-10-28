package com.intuit.biddingSystem.repository;

import com.intuit.biddingSystem.model.AuctionSlot;
import com.intuit.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuctionSlotRepository extends JpaRepository<AuctionSlot, UUID> {
    List<AuctionSlot> findByStatus(AuctionSlot.SlotStatus status);
    List<AuctionSlot> findByProductAndStatus(Product product, AuctionSlot.SlotStatus status);
}

