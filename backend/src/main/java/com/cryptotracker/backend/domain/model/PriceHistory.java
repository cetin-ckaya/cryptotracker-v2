package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "price_history")
@Getter
@Setter
public class PriceHistory extends BaseEntity {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "coin_id",nullable = false)
        private Coin coin;


        @Column(name = "price",nullable = false)
        private BigDecimal price;

        @Column(name = "recorded_at",nullable = false)
        private LocalDateTime recordedAt;
}

