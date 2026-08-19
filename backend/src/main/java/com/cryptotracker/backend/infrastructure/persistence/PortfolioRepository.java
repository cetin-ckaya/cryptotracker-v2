package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio,Long> {
    //Kullanıcının portföyünü bulmak için
    Optional<Portfolio> findByUserId(Long userId);
}
