package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.PortfolioMapper;
import com.cryptotracker.backend.application.dto.request.CreateTransactionRequest;
import com.cryptotracker.backend.application.dto.response.TransactionResponse;
import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.Coin;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.domain.model.Transaction;
import com.cryptotracker.backend.infrastructure.persistence.CoinRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final CoinRepository coinRepository;
    private final PortfolioMapper portfolioMapper;


    public TransactionService(TransactionRepository transactionRepository, PortfolioRepository portfolioRepository, CoinRepository coinRepository, PortfolioMapper portfolioMapper) {
        this.transactionRepository = transactionRepository;
        this.portfolioRepository = portfolioRepository;
        this.coinRepository = coinRepository;
        this.portfolioMapper = portfolioMapper;
    }

    // Yeni bir alım/satım kaydı oluşturur — transactions tablosuna INSERT
    public TransactionResponse addTransaction(Long userId, CreateTransactionRequest request) {
        // Kullanıcının portföyünü bul — yoksa 404
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        // İşlem yapılacak coini bul — yoksa 404
        Coin coin = coinRepository.findById(request.getCoinId())
                .orElseThrow(() -> new NotFoundException("Coin Not Found"));

        // totalAmount = quantity × pricePerUnit — DB'ye de kaydediyoruz
        // çünkü fiyat ileride değişse bile o anki tutarı tutmak gerekir
        BigDecimal totalAmount = request.getQuantity()
                .multiply(request.getPricePerUnit());

        // Transaction nesnesini oluştur ve alanları doldur
        Transaction transaction = new Transaction();
        transaction.setPortfolio(portfolio);
        transaction.setCoin(coin);
        transaction.setType(request.getType());
        transaction.setQuantity(request.getQuantity());
        transaction.setPricePerUnit(request.getPricePerUnit());
        transaction.setTotalAmount(totalAmount);
        // İşlem zamanı: şu an — client'tan göndermeye gerek yok, sunucu belirler
        transaction.setTransactionDate(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        // MapStruct ile DTO'ya çevir (PortfolioMapper'da toResponse(Transaction) metodu var)
        return portfolioMapper.toResponse(saved);

    }
    // Kullanıcının işlem geçmişini tarihe göre azalan sırayla döndürür
    // TransactionRepository'deki findByPortfolioId...Desc metodu sıralamayı halleder
    public List<TransactionResponse> getTransaction(Long userId){
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        List<Transaction> transactions =
                transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolio.getId());

        // Her Transaction → TransactionResponse (MapStruct stream mapping)
        return transactions.stream()
                .map(portfolioMapper::toResponse)
                .toList();
    }
}
