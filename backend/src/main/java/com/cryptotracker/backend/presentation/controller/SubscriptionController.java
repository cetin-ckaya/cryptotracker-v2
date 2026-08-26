package com.cryptotracker.backend.presentation.controller;

import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.application.service.SubscriptionService;
import com.cryptotracker.backend.application.service.UserService;
import com.cryptotracker.backend.domain.model.Subscription;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    // JWT'den email al, DB'den userId bul — diger controller'larla ayni pattern
    private Long getAuthenticatedUserId() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getId();
    }

    // Kullanicinin mevcut subscription tier'ini dondurur
    @GetMapping()
    public ResponseEntity<Subscription> getSubscription(){
        return ResponseEntity.ok(subscriptionService.getOrCreateSubscription(getAuthenticatedUserId()));
    }

    // Kullaniciyi PREMIUM'a yukselttir
    @PostMapping("/upgrade")
    public ResponseEntity<Subscription> upgradeToPremium(){
        return ResponseEntity.ok(subscriptionService.upgradeToPremium(getAuthenticatedUserId()));
    }
}
