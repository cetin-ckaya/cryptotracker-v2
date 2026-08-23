package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {
    Optional<Portfolio> findByUserId(Long userId);

    @Query("SELECT p FROM Portfolio p LEFT JOIN FETCH p.holdings WHERE p.user.id = :userId")
    Optional<Portfolio> findByUserIdWithHoldings(@Param("userId") Long userId);
}
