package com.intuit.biddingSystem.dto;

import com.intuit.biddingSystem.model.Winner;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class WinnerResponse {

    private UUID winnerId;
    private UUID slotId;
    private UUID userId;
    private BigDecimal winningBidAmount;
    private LocalDateTime notificationTime;

    public WinnerResponse(Winner winner) {
        this.winnerId = winner.getWinnerId();
        this.slotId = winner.getSlot().getSlotId();
        this.userId = winner.getUser().getUserId();
        this.winningBidAmount = winner.getBid().getBidAmount();
        this.notificationTime = winner.getNotificationTime();
    }

}
