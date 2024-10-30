package com.intuit.biddingSystem.auction.auctionslot.model;

import com.intuit.biddingSystem.auction.bid.model.Bid;
import com.intuit.biddingSystem.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "winners",
        indexes = {
                @Index(name = "idx_winner_slot_id", columnList = "slot_id"),
                @Index(name = "idx_winner_user_id", columnList = "user_id"),
                @Index(name = "idx_winner_bid_id", columnList = "bid_id")
        }
)
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID winnerId;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private AuctionSlot slot;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "bid_id", nullable = false, unique = true)
    private Bid bid;

    @Column(nullable = false)
    private Boolean notified = false;

    private LocalDateTime notificationTime;
}
