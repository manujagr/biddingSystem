package com.hrrev.biddingSystem.events;

import java.io.Serializable;
import java.util.UUID;

public class AuctionEndedEvent implements Serializable {
    private UUID slotId;
    private UUID productId;
    private UUID winningBidId;
    private UUID winnerUserId;

    public AuctionEndedEvent(UUID slotId, UUID productId, UUID winningBidId, UUID winnerUserId) {
        this.slotId = slotId;
        this.productId = productId;
        this.winningBidId = winningBidId;
        this.winnerUserId = winnerUserId;
    }

    // Getters and setters

    public UUID getSlotId() {
        return slotId;
    }

    public void setSlotId(UUID slotId) {
        this.slotId = slotId;
    }

    public UUID getWinningBidId() {
        return winningBidId;
    }

    public void setWinningBidId(UUID winningBidId) {
        this.winningBidId = winningBidId;
    }

    public UUID getWinnerUserId() {
        return winnerUserId;
    }

    public void setWinnerUserId(UUID winnerUserId) {
        this.winnerUserId = winnerUserId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
