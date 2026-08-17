package com.cryptotracker.backend.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coins")
@Getter
@Setter
public class Coin extends BaseEntity{
    @Column(name = "symbol",nullable = false)
    private String symbol;

    @Column(name ="name",nullable = false)
    private String name;

    @Column(name = "coingecko_id")
    private String coingeckoId;

    @Column(name = "icon_url")
    private String iconUrl;

    @Column(name = "is_free_tier_watchlist",columnDefinition = "boolean default false")
    private Boolean isFreeTierWatchlist =  false;
}
