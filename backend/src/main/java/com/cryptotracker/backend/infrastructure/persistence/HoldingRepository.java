package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Holding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding,Long> {
    //Portföydeki tüm varlıkları getir
    List<Holding> findByPortfolioId(Long portfolioId);
}
