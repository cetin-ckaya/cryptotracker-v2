package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.core.ObjectReadContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_value_history")
@Getter
@Setter
public class PortfolioValueHistory extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id",nullable = false)
    private Portfolio portfolio;

    @Column(name = "total_value",nullable = false)
    private BigDecimal totalValue;

    @Column(name = "recorded_at",nullable = false)
    private LocalDateTime recordedAt;
}
