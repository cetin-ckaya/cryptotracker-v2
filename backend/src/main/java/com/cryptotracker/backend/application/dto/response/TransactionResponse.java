package com.cryptotracker.backend.application.dto.response;

import com.cryptotracker.backend.domain.model.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// İşlem geçmişi tablosunda her satır bu DTO'yu temsil eder.
@Getter
@Setter
public class TransactionResponse {

    private Long id;

    // Hangi coin için işlem yapıldı?
    private Long coinId;
    private String coinSymbol;  // "BTC", "ETH" gibi — tabloda gösterilir

    // BUY mi SELL mi? Dashboard'da renklendirme için (yeşil/kırmızı) kullanılır.
    private TransactionType type;

    private BigDecimal quantity;
    private BigDecimal pricePerUnit;

    // quantity × pricePerUnit — toplam işlem tutarı
    private BigDecimal totalAmount;

    // İşlemin yapıldığı zaman — tarihe göre DESC sıralı liste için gerekli
    private LocalDateTime transactionDate;
}