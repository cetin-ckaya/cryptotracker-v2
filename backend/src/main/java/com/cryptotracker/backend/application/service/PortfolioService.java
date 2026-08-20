package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.request.AddHoldingRequest;
import com.cryptotracker.backend.application.dto.response.HoldingResponse;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {

    public PortfolioResponse getPortfolio(Long userId){
        return null;
    }

    public HoldingResponse addHolding(Long userId, AddHoldingRequest request){
        return null;
    }

    public void removeHolding(Long userId, Long holdingId){
    }
}
