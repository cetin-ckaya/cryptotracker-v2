package com.cryptotracker.backend.infrastructure.external;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

@Service
public class CoinGeckoService {
    private final RestClient restClient;

    public CoinGeckoService() {
        this.restClient = RestClient.create();
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
    public BigDecimal getPrice(String symbol){
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
}

