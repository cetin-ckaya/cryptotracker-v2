package com.cryptotracker.backend.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HoldingResponse {
    private Long id;
    private Long coinId;
    private String coinSymbol;
    private String coinName;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;

}
