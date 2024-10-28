package com.intuit.biddingSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "bids",
        indexes = {
                @Index(name = "idx_bid_slot_id", columnList = "slot_id"),
                @Index(name = "idx_bid_user_id", columnList = "user_id"),
                @Index(name = "idx_bid_bid_amount", columnList = "bidAmount")
        }
)
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bidId;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private AuctionSlot slot;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private LocalDateTime bidTime;
}
