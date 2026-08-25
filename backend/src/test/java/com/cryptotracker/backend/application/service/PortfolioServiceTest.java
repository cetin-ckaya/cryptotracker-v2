package com.cryptotracker.backend.application.service;


import com.cryptotracker.backend.application.dto.PortfolioMapper;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.Coin;
import com.cryptotracker.backend.domain.model.Holding;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.infrastructure.external.CoinGeckoService;
import com.cryptotracker.backend.infrastructure.persistence.CoinRepository;
import com.cryptotracker.backend.infrastructure.persistence.HoldingRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

// @ExtendWith: Mockito'yu JUnit 5 ile entegre eder
@ExtendWith(MockitoExtension.class)
public class PortfolioServiceTest {
    // @Mock: Gerçek bean yerine sahte nesne — HTTP isteği gitmez, DB sorgusu gitmez
    @Mock private PortfolioRepository portfolioRepository;
    @Mock private HoldingRepository holdingRepository;
    @Mock private CoinRepository coinRepository;
    @Mock private UserRepository userRepository;
    @Mock private PortfolioMapper portfolioMapper;
    @Mock private CoinGeckoService coinGeckoService;

    // @InjectMocks: Yukaridaki mock'lari constructor'a inject ederek gercek service'i olusturur
    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void getPortfolio_whenPortfolioNotFound_throwsNotFoundException() {
        // GIVEN — kullanici portfoyu yok
        when(portfolioRepository.findByUserIdWithHoldings(99L)).thenReturn(Optional.empty());

        // WHEN & THEN — NotFoundException firlatilmali
        assertThatThrownBy(() -> portfolioService.getPortfolio(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Portfolio Not Found");
    }

    @Test
    void getPortfolio_calculatesProfitLossCorrectly() {
        // GIVEN — 0.5 BTC, alis fiyati 50000, guncel fiyat 60000
        Coin btc = new Coin();
        btc.setSymbol("BTC");

        Holding holding = new Holding();
        holding.setCoin(btc);
        holding.setQuantity(new BigDecimal("0.5"));
        holding.setAverageBuyPrice(new BigDecimal("50000"));

        Portfolio portfolio = new Portfolio();
        portfolio.setHoldings(List.of(holding));

        // Mock'lar ne dondurecek?
        when(portfolioRepository.findByUserIdWithHoldings(1L)).thenReturn(Optional.of(portfolio));
        when(portfolioMapper.toPortfolioResponse(portfolio)).thenReturn(new PortfolioResponse());
        // CoinGecko mock — gercek HTTP isteği gitmez
        when(coinGeckoService.getPrice("BTC")).thenReturn(new BigDecimal("60000"));

        // WHEN
        PortfolioResponse response = portfolioService.getPortfolio(1L);

        // THEN — 0.5 * 60000 = 30000 deger, 0.5 * 50000 = 25000 maliyet, kar = 5000
        assertThat(response.getTotalValue()).isEqualByComparingTo("30000");
        assertThat(response.getTotalInvested()).isEqualByComparingTo("25000");
        assertThat(response.getTotalProfitLoss()).isEqualByComparingTo("5000");
    }

}
