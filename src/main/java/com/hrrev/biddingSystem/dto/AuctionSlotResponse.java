package com.hrrev.biddingSystem.dto;

import com.hrrev.biddingSystem.model.AuctionSlot;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuctionSlotResponse {

    private UUID slotId;
    private ProductResponse product;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AuctionSlotResponse(AuctionSlot slot) {
        this.slotId = slot.getSlotId();
        this.product = new ProductResponse(slot.getProduct());
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.status = slot.getStatus().name();
        this.createdAt = slot.getCreatedAt();
        this.updatedAt = slot.getUpdatedAt();
    }

    // Getters and Setters
}

