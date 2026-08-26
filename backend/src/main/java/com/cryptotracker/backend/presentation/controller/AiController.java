package com.cryptotracker.backend.presentation.controller;

import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.application.service.AiAnalysisService;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiAnalysisService aiAnalysisService;
    private final UserRepository userRepository;

    public AiController(AiAnalysisService aiAnalysisService, UserRepository userRepository) {
        this.aiAnalysisService = aiAnalysisService;
        this.userRepository = userRepository;
    }

    // JWT'den email al, DB'den userId bul — diger controller'larla ayni pattern
    private Long getAuthenticatedUserId() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getId();
    }

    // @PreAuthorize: Spring bu metodu cagirmadan once JWT'deki role'e bakar.
    // ROLE_PREMIUM degilse 403 Forbidden doner — service katmanina hic ulasmaz.
    @PreAuthorize("hasRole('PREMIUM')")
    @GetMapping("/analyze")
    public ResponseEntity<String> analyzePortfolio() {
        String analysis = aiAnalysisService.analyzePortfolio(getAuthenticatedUserId());
        return ResponseEntity.ok(analysis);
    }
}