package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.PortfolioValueHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioValueHistoryRepository extends JpaRepository<PortfolioValueHistory,Long> {
}
