package com.cryptotracker.backend.application.dto.request;

import com.cryptotracker.backend.domain.model.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateTransactionRequest {
    @NotNull()
    private Long coinId;

    @NotNull()
    private TransactionType type;

    @NotNull()
    private BigDecimal quantity;

    @NotNull()
    private BigDecimal pricePerUnit;
}
