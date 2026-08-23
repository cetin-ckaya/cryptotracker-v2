package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.PortfolioMapper;
import com.cryptotracker.backend.application.dto.request.AddHoldingRequest;
import com.cryptotracker.backend.application.dto.response.HoldingResponse;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.Coin;
import com.cryptotracker.backend.domain.model.Holding;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.infrastructure.persistence.CoinRepository;
import com.cryptotracker.backend.infrastructure.persistence.HoldingRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;

    public PortfolioService(PortfolioRepository portfolioRepository, HoldingRepository holdingRepository, CoinRepository coinRepository, UserRepository userRepository, PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.portfolioMapper = portfolioMapper;
    }


    public PortfolioResponse getPortfolio(Long userId){
        // Kullanıcının portföyünü bul — yoksa 404
        Portfolio portfolio = portfolioRepository.findByUserIdWithHoldings(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        // MapStruct ile DTO'ya çevir
        PortfolioResponse response = portfolioMapper.toPortfolioResponse(portfolio);

        // Kar/zarar hesabı Phase 2'de (fiyat API'si gelince) yapılacak — şimdilik sıfır
        response.setTotalValue(BigDecimal.ZERO);
        response.setTotalInvested(BigDecimal.ZERO);
        response.setTotalProfitLoss(BigDecimal.ZERO);
        response.setTotalProfitLossPercentage(BigDecimal.ZERO);

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
