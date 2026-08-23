package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.PortfolioMapper;
import com.cryptotracker.backend.application.dto.request.AddHoldingRequest;
import com.cryptotracker.backend.application.dto.response.HoldingResponse;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;
    private final CoinGeckoService coinGeckoService;

    public PortfolioService(PortfolioRepository portfolioRepository, HoldingRepository holdingRepository, CoinRepository coinRepository, UserRepository userRepository, PortfolioMapper portfolioMapper, CoinGeckoService coinGeckoService) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.portfolioMapper = portfolioMapper;
        this.coinGeckoService = coinGeckoService;
    }


    public PortfolioResponse getPortfolio(Long userId){
        // Kullanıcının portföyünü bul — yoksa 404
        Portfolio portfolio = portfolioRepository.findByUserIdWithHoldings(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        // MapStruct ile DTO'ya çevir
        PortfolioResponse response = portfolioMapper.toPortfolioResponse(portfolio);

        // Her holding için: anlık fiyat × miktar = güncel değer
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for(Holding holding : portfolio.getHoldings()){
            BigDecimal currentPrice = coinGeckoService.getPrice(holding.getCoin().getSymbol());
            BigDecimal currentValue = currentPrice.multiply(holding.getQuantity());
            BigDecimal invested = holding.getAverageBuyPrice().multiply(holding.getQuantity());


            totalValue = totalValue.add(currentValue);
            totalInvested = totalInvested.add(invested);
        }
        BigDecimal profitLoss = totalValue.subtract(totalInvested);
        BigDecimal profitLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        response.setTotalValue(totalValue);
        response.setTotalInvested(totalInvested);
        response.setTotalProfitLoss(profitLoss);
        response.setTotalProfitLossPercentage(profitLossPercentage);

        return response;
    }

    public HoldingResponse addHolding(Long userId, AddHoldingRequest request){
        // Kullanıcının ekleyeceği coini bul — yoksa 404
        Coin coin = coinRepository.findById(request.getCoinId())
                .orElseThrow(() -> new NotFoundException("Coin Not Found"));

        //Portföyü bul — yoksa NotFoundException fırlat
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        //Yeni Holding oluştur ve alanları set et
        Holding holding = new Holding();
        holding.setCoin(coin);
        holding.setPortfolio(portfolio);
        holding.setQuantity(request.getQuantity());
        holding.setAverageBuyPrice(request.getAverageBuyPrice());

        //DB'ye kaydet
        Holding savedHolding = holdingRepository.save(holding);

        //DTO'ya çevir ve döndür
        return  portfolioMapper.toHoldingResponse(savedHolding);

    }

    public void removeHolding(Long userId, Long holdingId){
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new NotFoundException("Holding Not Found"));

        holdingRepository.delete(holding);

    }
}
