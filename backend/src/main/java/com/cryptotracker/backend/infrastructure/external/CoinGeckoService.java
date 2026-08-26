package com.cryptotracker.backend.infrastructure.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CoinGeckoService {
    private final RestClient restClient;

    private static final Logger log = LoggerFactory.getLogger(CoinGeckoService.class);

    // SimpleClientHttpRequestFactory: Java'nin eski HttpURLConnection'ini kullanir
    // Spring Boot 4.x'te yeni HttpClient Windows'ta loopback baglantisindan hata verir
    public CoinGeckoService() {
        this.restClient = RestClient.builder()
                .requestFactory(new SimpleClientHttpRequestFactory())
                .build();
    }

    // CoinGecko'da her coin'in kendine özgü bir "id"si var.
    // BTC → "bitcoin", ETH → "ethereum", SOL → "solana"
    private String toCoinGeckoId(String symbol){
        return switch (symbol.toUpperCase()){
            case "BTC" -> "bitcoin";
            case "ETH" -> "ethereum";
            case "SOL" -> "solana";
            default -> symbol.toLowerCase();
        };
    }

    // Verilen sembol için anlık USD fiyatını döndürür.
    // Örnek: getPrice("BTC") → 95000.00
    @SuppressWarnings("unchecked")
    //Bu anotasyon şunu yapar: symbol parametresini key olarak kullanarak sonucu "prices" adlı cache'e yazar.
    // Aynı symbol ile tekrar çağrılınca metot gövdesi çalışmaz, Redis'ten döner.
    @Cacheable(value = "prices",key = "#symbol")
    public BigDecimal getPrice(String symbol){
        log.info("CoinGecko API called for: {}", symbol);

        String coinId = toCoinGeckoId(symbol);
        String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + coinId + "&vs_currencies=usd";

        Map<String, Map<String, Object>> response = restClient.get()
                .uri(url)
                .retrieve()
                .body(Map.class);

        if (response == null || !response.containsKey(coinId)) {
            return BigDecimal.ZERO;
        }

        Object price = response.get(coinId).get("usd");
        return new BigDecimal(price.toString());
    }
    //tüm fiyat cache'ini temizler
    @CacheEvict(value = "prices",allEntries = true)
    public void evictPriceCache(){
    }
}

