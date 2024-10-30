package com.intuit.biddingSystem.auction.auctionslot.repository;

import com.intuit.biddingSystem.auction.auctionslot.model.AuctionSlot;
import com.intuit.biddingSystem.auction.auctionslot.model.Winner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WinnerRepository extends JpaRepository<Winner, UUID> {
    Optional<Winner> findBySlot(AuctionSlot slot);
}
