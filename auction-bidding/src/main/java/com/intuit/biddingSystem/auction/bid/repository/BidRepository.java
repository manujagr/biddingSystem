package com.intuit.biddingSystem.auction.bid.repository;

import com.intuit.biddingSystem.auction.auctionslot.model.AuctionSlot;
import com.intuit.biddingSystem.auction.bid.model.Bid;
import com.intuit.biddingSystem.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findBySlot(AuctionSlot slot);
    Optional<Bid> findTopBySlotOrderByBidAmountDesc(AuctionSlot slot);
    @Query("SELECT b FROM Bid b WHERE b.slot.product = :product")
    Set<Bid> findByProduct(com.intuit.biddingSystem.product.model.Product product);

    @Query("select bid.user from Bid bid where bid.slot= :slot")
    List<User> findBidderUsersBySlot(AuctionSlot slot);
}


