package com.hrrev.biddingSystem.model;

import jakarta.persistence.*;

import javax.management.relation.Role;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bids")
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

    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    // Getters and Setters
}
