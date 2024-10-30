package com.intuit.biddingSystem.auction.bid.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class BidMessage implements Serializable {
    private UUID auctionId;
    private UUID userId;
    private BigDecimal amount;
    private LocalDateTime bidTime;
}

