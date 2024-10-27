package com.hrrev.biddingSystem.repository;

import com.hrrev.biddingSystem.model.Winner;
import com.hrrev.biddingSystem.model.AuctionSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    Optional<Winner> findBySlot(AuctionSlot slot);
}
