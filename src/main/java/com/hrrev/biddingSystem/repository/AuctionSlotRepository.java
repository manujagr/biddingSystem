package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.AuctionSlot;
import com.hrrev.biddingSystem.model.Category;
import com.hrrev.biddingSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionSlotRepository extends JpaRepository<AuctionSlot, Long> {
    List<AuctionSlot> findByStatus(AuctionSlot.SlotStatus status);
    List<AuctionSlot> findByEndTimeBeforeAndStatus(LocalDateTime now, AuctionSlot.SlotStatus status);
}

