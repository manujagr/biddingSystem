package com.intuit.biddingSystem.repository;

import com.intuit.biddingSystem.model.Winner;
import com.intuit.biddingSystem.model.AuctionSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    Optional<Winner> findBySlot(AuctionSlot slot);
}
