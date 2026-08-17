package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "holdings")
@Getter
@Setter
public class Holding extends BaseEntity {

    //unique = true
    //@OneToOne	 Genelde evet (örn: Portfolio.user_id — bir user'ın sadece 1 portföyü olsun istiyoruz)
    //@ManyToOne    Hayır (many tarafı zaten "çok kere tekrarlanabilsin" anlamına gelir, unique bunu engeller)
    @ManyToOne
    @JoinColumn(name = "portfolio_id",nullable = false)
    private Portfolio portfolio;

    @ManyToOne
    @JoinColumn(name = "coin_id",nullable = false)
    private Coin coin;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "average_buy_price")
    private BigDecimal averageBuyPrice;
}
