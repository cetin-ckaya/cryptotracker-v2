package com.cryptotracker.backend.presentation.controller;

import com.cryptotracker.backend.application.dto.request.CreateTransactionRequest;
import com.cryptotracker.backend.application.dto.response.TransactionResponse;
import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.application.service.TransactionService;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    private Long getAuthenticatedUserId() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getId();
    }

    @PostMapping()
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return ResponseEntity.status(201).body(transactionService.addTransaction(getAuthenticatedUserId(), request));
    }

    @GetMapping()
    public ResponseEntity<List<TransactionResponse>> getTransaction(){
        return ResponseEntity.status(200).body(transactionService.getTransaction(getAuthenticatedUserId()));
    }
}
