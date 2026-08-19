package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory,Long> {
}
