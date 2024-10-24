package com.hrrev.biddingSystem.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auction_slots")
public class AuctionSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID slotId;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SlotStatus status; // Scheduled, Active, Completed

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters

    public enum SlotStatus {
        SCHEDULED,
        ACTIVE,
        COMPLETED
    }
}
