package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    //İşlem geçmişini tarihe göre sıralı getir
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);
}
