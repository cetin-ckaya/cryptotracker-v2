package com.cryptotracker.backend.infrastructure.scheduler;

import com.cryptotracker.backend.domain.model.PortfolioValueHistory;
import com.cryptotracker.backend.infrastructure.external.CoinGeckoService;
import com.cryptotracker.backend.infrastructure.persistence.CoinRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioValueHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// @Component: Spring bu sinifi otomatik algilayip bean olarak kaydeder
@Component
public class MarketScheduler {
    private static final Logger log = LoggerFactory.getLogger(MarketScheduler.class);

    private final CoinGeckoService coinGeckoService;
    private final CoinRepository coinRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioValueHistoryRepository portfolioValueHistoryRepository;

    public MarketScheduler(CoinGeckoService coinGeckoService,CoinRepository coinRepository, PortfolioRepository portfolioRepository, PortfolioValueHistoryRepository portfolioValueHistoryRepository) {
        this.coinGeckoService = coinGeckoService;
        this.coinRepository = coinRepository;
        this.portfolioRepository = portfolioRepository;
        this.portfolioValueHistoryRepository = portfolioValueHistoryRepository;
    }

    // Her 5 dakikada bir calisir — Redis cache'i temizleyip tum coinlerin fiyatini yeniden ceker
    // cron = "0 */5 * * * *" : her saatin 0., 5., 10. ... dakikasinda tetiklenir
    @Scheduled(cron = "0 */5 * * * *")
    public void refreshPrice(){
        log.info("Scheduled price refresh started");

        // Cache'i temizle — bir sonraki getPrice() cagrisi gercek HTTP istegi yapacak
        coinGeckoService.evictPriceCache();

        // DB'deki tum coinler icin fiyati yeniden cek — cache'e yazar
        coinRepository.findAll().forEach(coin -> {
            coinGeckoService.getPrice(coin.getSymbol());
            log.info("Price refreshed for: {}", coin.getSymbol());
        });
        log.info("Scheduled price refresh completed");
    }

    // Her saatin basinda calisir — tum portfolylerin anlık degerini DB'ye kaydeder
    // Dashboard'da "gecen haftaya gore nasil degisti" grafigi icin kullanilir
    @Scheduled(cron = "0 0 * * * *")
    public void savePortfolioHistory(){
        log.info("Portfolio history save started");

        portfolioRepository.findAll().forEach(portfolio -> {
            // Her holding icin: guncel fiyat x miktar = anlık deger
            BigDecimal totalValue = portfolio.getHoldings().stream()
                    .map(holding -> coinGeckoService.getPrice(holding.getCoin().getSymbol())
                            .multiply(holding.getQuantity()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Snapshot olustur ve kaydet
            PortfolioValueHistory history = new PortfolioValueHistory();
            history.setPortfolio(portfolio);
            history.setTotalValue(totalValue);
            history.setRecordedAt(LocalDateTime.now());

            portfolioValueHistoryRepository.save(history);
            log.info("Portfolio history saved for portfolioId: {}", portfolio.getId());
        });
        log.info("Portfolio history save completed");
    }
}
