package com.hrrev.biddingSystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BidRegistrationRequest {
    @NotNull
    private Long slotId;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal bidAmount;

}
