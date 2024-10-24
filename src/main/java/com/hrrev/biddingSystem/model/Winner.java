package com.hrrev.biddingSystem.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "winners")
public class Winner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID winnerId;

    @OneToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private AuctionSlot slot;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "bid_id", nullable = false)
    private Bid bid;

    private Boolean notified = false;
    private LocalDateTime notificationTime;

    // Getters and Setters
}
