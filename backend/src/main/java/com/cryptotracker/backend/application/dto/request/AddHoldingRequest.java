package com.cryptotracker.backend.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
//Kullanıcı portföyüne coin eklerken

@Getter
@Setter
public class AddHoldingRequest {
    @NotNull()
    private Long coinId;

    @NotNull()
    private BigDecimal quantity;

    @NotNull()
    private BigDecimal averageBuyPrice;
}
