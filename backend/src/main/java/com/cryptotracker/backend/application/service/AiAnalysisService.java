package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiAnalysisService {

    // application.properties'den okunur: groq.api.key=gsk_...
    @Value("${groq.api.key}")
    private String apiKey;

    private final PortfolioRepository portfolioRepository;

    // CoinGeckoService ile ayni yaklasim — Windows loopback sorununu onler
    private final RestClient restClient = RestClient.builder()
            .requestFactory(new SimpleClientHttpRequestFactory())
            .build();

    public AiAnalysisService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    public String analyzePortfolio(Long userId) {
        // Kullanicinin portfoyunu holdings ile birlikte getir
        Portfolio portfolio = portfolioRepository.findByUserIdWithHoldings(userId)
                .orElseThrow(() -> new NotFoundException("Portfolio not found"));

        // Holdings listesini okunabilir metne donustur
        // Ornek: "BTC: 2.5 adet, ETH: 1.0 adet"
        String holdingsSummary = portfolio.getHoldings().stream()
                .map(h -> h.getCoin().getSymbol() + ": " + h.getQuantity() + " adet")
                .collect(Collectors.joining(", "));

        if (holdingsSummary.isBlank()) {
            return "Portfoyunuzde henuz hic varlik yok. Analiz yapilamadi.";
        }

        // Groq'a gonderilecek kullanici mesaji
        String userPrompt = "Kullanicinin kripto portfoyu: " + holdingsSummary +
                ". Bu portfoyu kisaca analiz et, guclu ve zayif yonlerini belirt. " +
                "Cevabinin sonuna su uyariyi ekle: 'Bu analiz yatirim tavsiyesi degildir.'";

        // Groq API, OpenAI ile ayni formati kullanir
        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b",   // Groq developer plan'da kullanilabilir model
                "messages", List.of(
                        Map.of("role", "system",
                                "content", "Sen deneyimli bir kripto portfoy analistsin. Turkce cevap ver."),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        // Groq API'yi cagir — OpenAI ile ayni endpoint formati, sadece URL farkli
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .uri("https://api.groq.com/openai/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        // Gelen JSON'dan analiz metnini cikart
        // Yapi: response -> choices[0] -> message -> content
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        @SuppressWarnings("unchecked")
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");
    }
}