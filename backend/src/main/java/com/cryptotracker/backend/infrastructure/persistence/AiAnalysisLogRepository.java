package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.AiAnalysisLog;
import org.springframework.data.jpa.repository.JpaRepository;

//Normalde veritabanından veri çekmek için SQL sorgusu yazman gerekir:
//Bu hem çok uzun hem tekrarlayan hem de hataya açık kod. Her tablo için aynı şeyi yazmak zorunda kalırsın.
//
//JpaRepository bunu tamamen ortadan kaldırıyor:
//SQL yazmadın, JDBC bağlantısı açmadın, ResultSet okumadın — Spring Data JPA bunların hepsini arka planda otomatik yaptı.
public interface AiAnalysisLogRepository extends JpaRepository<AiAnalysisLog,Long> {
}
