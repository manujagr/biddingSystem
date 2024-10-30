package com.intuit.biddingSystem.auction.auctionslot.model;

import com.intuit.biddingSystem.product.model.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "auction_slots",
        indexes = {
                @Index(name = "idx_product_id", columnList = "product_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_start_time", columnList = "start_time")
        })
public class AuctionSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID slotId;

    @OneToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SlotStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public enum SlotStatus {
        SCHEDULED,
        ACTIVE,
        COMPLETED
    }
}