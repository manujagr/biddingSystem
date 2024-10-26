package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuctionSlotRepository extends JpaRepository<AuctionSlot, UUID> {
    List<AuctionSlot> findByStatus(AuctionSlot.SlotStatus status);
    List<AuctionSlot> findByProductAndStatus(Product product, AuctionSlot.SlotStatus status);
}

