package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.PortfolioMapper;
import com.cryptotracker.backend.application.dto.request.AddHoldingRequest;
import com.cryptotracker.backend.application.dto.response.HoldingResponse;
import com.cryptotracker.backend.application.dto.response.PortfolioResponse;
import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.Coin;
import com.cryptotracker.backend.domain.model.Holding;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.domain.model.PortfolioValueHistory;
import com.cryptotracker.backend.infrastructure.external.CoinGeckoService;
import com.cryptotracker.backend.infrastructure.persistence.CoinRepository;
import com.cryptotracker.backend.infrastructure.persistence.HoldingRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioValueHistoryRepository;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;
    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final PortfolioMapper portfolioMapper;
    private final CoinGeckoService coinGeckoService;
    // Gunluk kar/zarar icin 24 saat onceki portfoy degeri buradan okunur.
    // Tabloyu MarketScheduler.savePortfolioHistory() saat basi dolduruyor.
    private final PortfolioValueHistoryRepository portfolioValueHistoryRepository;

    public PortfolioService(PortfolioRepository portfolioRepository, HoldingRepository holdingRepository, CoinRepository coinRepository, UserRepository userRepository, PortfolioMapper portfolioMapper, CoinGeckoService coinGeckoService, PortfolioValueHistoryRepository portfolioValueHistoryRepository) {
        this.portfolioRepository = portfolioRepository;
        this.holdingRepository = holdingRepository;
        this.coinRepository = coinRepository;
        this.userRepository = userRepository;
        this.portfolioMapper = portfolioMapper;
        this.coinGeckoService = coinGeckoService;
        this.portfolioValueHistoryRepository = portfolioValueHistoryRepository;
    }


    public PortfolioResponse getPortfolio(Long userId){
        // Kullanıcının portföyünü bul — yoksa 404
        Portfolio portfolio = portfolioRepository.findByUserIdWithHoldings(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        // MapStruct ile DTO'ya çevir
        PortfolioResponse response = portfolioMapper.toPortfolioResponse(portfolio);

        // Her holding için: anlık fiyat × miktar = güncel değer
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalInvested = BigDecimal.ZERO;

        for(Holding holding : portfolio.getHoldings()){
            BigDecimal currentPrice = coinGeckoService.getPrice(holding.getCoin().getSymbol());
            BigDecimal currentValue = currentPrice.multiply(holding.getQuantity());
            BigDecimal invested = holding.getAverageBuyPrice().multiply(holding.getQuantity());


            totalValue = totalValue.add(currentValue);
            totalInvested = totalInvested.add(invested);
        }
        BigDecimal profitLoss = totalValue.subtract(totalInvested);
        BigDecimal profitLossPercentage = totalInvested.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : profitLoss.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        response.setTotalValue(totalValue);
        response.setTotalInvested(totalInvested);
        response.setTotalProfitLoss(profitLoss);
        response.setTotalProfitLossPercentage(profitLossPercentage);

        // --- Gunluk kar/zarar ---
        // totalProfitLoss "aldigindan bu yana" kumulatif kar/zarar.
        // Gunluk olan ise: simdiki deger - 24 saat onceki deger.
        applyDailyChange(response, portfolio.getId(), totalValue);

        return response;
    }

    // 24 saat onceki snapshot'a gore gunluk degisimi hesaplar ve response'a yazar.
    //
    // Tam 24 saat once kaydedilmis bir satir olmayabilir (scheduler saat basi calisiyor,
    // uygulama kapaliyken hic calismiyor). Bu yuzden "24 saat oncesinden daha eski
    // kayitlar icinde en yenisi" aliniyor — yani mevcut en yakin snapshot.
    //
    // Hic snapshot yoksa alanlar null birakilir; frontend bunu "Son 24 saatlik kayit
    // bekleniyor" olarak gosterir. Sifir yazmak yaniltici olurdu: "degisim yok" ile
    // "veri yok" ayni sey degil.
    private void applyDailyChange(PortfolioResponse response, Long portfolioId, BigDecimal totalValue) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        Optional<PortfolioValueHistory> snapshot = portfolioValueHistoryRepository
                .findFirstByPortfolioIdAndRecordedAtBeforeOrderByRecordedAtDesc(portfolioId, twentyFourHoursAgo);

        if (snapshot.isEmpty()) {
            return; // dailyProfitLoss ve dailyProfitLossPercentage null kalir
        }

        BigDecimal previousValue = snapshot.get().getTotalValue();
        BigDecimal dailyProfitLoss = totalValue.subtract(previousValue);
        response.setDailyProfitLoss(dailyProfitLoss);

        // Sifira bolme korumasi: eski deger 0 ise yuzde tanimsizdir, null birak.
        // BigDecimal'da == veya equals yerine compareTo kullanilir; equals scale'i de
        // karsilastirir (0 ile 0.00 esit sayilmaz), compareTo sadece degere bakar.
        if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        // BigDecimal'da divide() olcek + yuvarlama verilmezse bolum sonsuz basamakli
        // ciktiginda ArithmeticException firlatir — bu yuzden 4 basamak + HALF_UP.
        BigDecimal dailyPercentage = dailyProfitLoss
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        response.setDailyProfitLossPercentage(dailyPercentage);
    }

    public HoldingResponse addHolding(Long userId, AddHoldingRequest request){
        // Kullanıcının ekleyeceği coini bul — yoksa 404
        Coin coin = coinRepository.findById(request.getCoinId())
                .orElseThrow(() -> new NotFoundException("Coin Not Found"));

        //Portföyü bul — yoksa NotFoundException fırlat
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio Not Found"));

        //Yeni Holding oluştur ve alanları set et
        Holding holding = new Holding();
        holding.setCoin(coin);
        holding.setPortfolio(portfolio);
        holding.setQuantity(request.getQuantity());
        holding.setAverageBuyPrice(request.getAverageBuyPrice());

        //DB'ye kaydet
        Holding savedHolding = holdingRepository.save(holding);

        //DTO'ya çevir ve döndür
        return  portfolioMapper.toHoldingResponse(savedHolding);

    }

    public void removeHolding(Long userId, Long holdingId){
        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new NotFoundException("Holding Not Found"));

        holdingRepository.delete(holding);

    }
}
