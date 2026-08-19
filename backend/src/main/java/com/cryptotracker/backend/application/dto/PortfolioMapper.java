package com.cryptotracker.backend.application.dto;

import com.cryptotracker.backend.application.dto.response.HoldingResponse;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import com.cryptotracker.backend.application.dto.response.TransactionResponse;
import com.cryptotracker.backend.domain.model.Holding;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    //HoldingResponse toHoldingResponse(Holding holding) → dikkat:
    // Holding.coin.symbol ve Holding.coin.name alanları
    // HoldingResponse'da coinSymbol ve coinName olarak eşleşmiyor. @Mapping anotasyonu gerekiyor:
    @Mapping(source = "coin.symbol", target = "coinSymbol")
    @Mapping(source = "coin.name", target = "coinName")
    @Mapping(source = "coin.id", target = "coinId")
    HoldingResponse toHoldingResponse(Holding holding);

    //PortfolioResponse toPortfolioResponse(Portfolio portfolio) →
    // totalValue, totalInvested, totalProfitLoss, totalProfitLossPercentage alanları entity'de yok
    PortfolioResponse toPortfolioResponse(Portfolio portfolio);

    @Mapping(source = "coin.symbol", target = "coinSymbol")
    TransactionResponse toResponse(Transaction transaction);
}
