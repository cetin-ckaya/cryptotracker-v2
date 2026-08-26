package com.cryptotracker.backend.infrastructure.messaging;

import java.math.BigDecimal;

// Record: sadece veri tasimak icin kullanilan immutable sinif
// Constructor, getter, equals, hashCode otomatik uretilir
public record PriceUpdateMessage(String symbol, BigDecimal price) {}