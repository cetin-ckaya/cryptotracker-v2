package com.cryptotracker.backend.presentation.controller;


import com.cryptotracker.backend.application.dto.request.AddHoldingRequest;
import com.cryptotracker.backend.application.dto.response.HoldingResponse;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import com.cryptotracker.backend.application.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public ResponseEntity<PortfolioResponse> getPortfolio(){
        return ResponseEntity.ok(portfolioService.getPortfolio(1L));
    }

    @PostMapping("/holdings")
    public ResponseEntity<HoldingResponse> addHolding(@RequestBody AddHoldingRequest request){
        return ResponseEntity.status(201).body(portfolioService.addHolding(1L, request));
    }

    @DeleteMapping("/holdings/{holdingId}")
    public ResponseEntity<Void> removeHolding(@PathVariable Long holdingId){
        portfolioService.removeHolding(1L, holdingId);
        return ResponseEntity.noContent().build();
    }
}

