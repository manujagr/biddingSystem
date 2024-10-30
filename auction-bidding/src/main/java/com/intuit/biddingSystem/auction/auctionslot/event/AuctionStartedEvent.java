package com.intuit.biddingSystem.auction.auctionslot.event;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Data
public class AuctionStartedEvent implements Serializable {
    private UUID slotId;
    private UUID productId;

    public AuctionStartedEvent(UUID slotId, UUID productId) {
        this.slotId = slotId;
        this.productId = productId;
    }

}
