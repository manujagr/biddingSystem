package com.intuit.biddingSystem.events;

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
}
