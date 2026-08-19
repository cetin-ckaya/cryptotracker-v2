package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoinRepository extends JpaRepository<Coin,Long> {
    //FREE kullanıcıya gösterilecek sabit coin listesi
    List<Coin> findByIsFreeTierWatchlistTrue();
}
