package com.intuit.biddingSystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class BidRegistrationRequest {
    @NotNull
    private UUID slotId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal bidAmount;

}
