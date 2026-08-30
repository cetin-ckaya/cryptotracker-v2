package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.PortfolioValueHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PortfolioValueHistoryRepository extends JpaRepository<PortfolioValueHistory, Long> {

    // Verilen andan once kaydedilmis EN YENI snapshot'i getirir.
    // Gunluk kar/zarar icin "24 saat oncesinin portfoy degeri" boyle bulunur.
    // Optional: hic snapshot yoksa (yeni kullanici, scheduler henuz calismadi)
    // null yerine bos Optional doner — servis katmani bunu kontrol etmek zorunda kalir.
    Optional<PortfolioValueHistory> findFirstByPortfolioIdAndRecordedAtBeforeOrderByRecordedAtDesc(
            Long portfolioId,
            LocalDateTime before
    );
}