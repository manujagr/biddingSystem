package com.hrrev.biddingSystem.events;

import java.io.Serializable;
import java.util.UUID;

public class AuctionStartedEvent implements Serializable {
    private UUID slotId;
    private UUID productId;

    public AuctionStartedEvent(UUID slotId, UUID productId) {
        this.slotId = slotId;
        this.productId = productId;
    }

    public UUID getSlotId() {
        return slotId;
    }

    public void setSlotId(UUID slotId) {
        this.slotId = slotId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }
}
