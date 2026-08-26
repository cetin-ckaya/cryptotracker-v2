package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    //İşlem geçmişini tarihe göre sıralı getir
    // @EntityGraph: Spring Data'ya "bu sorguyu çekerken coin'i de JOIN ile getir" der
    // attributePaths = hangi ilişkilerin JOIN FETCH ile yükleneceğini belirtir
    @EntityGraph(attributePaths = {"coin"})
    List<Transaction> findByPortfolioIdOrderByTransactionDateDesc(Long portfolioId);
}
