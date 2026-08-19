package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription,Long> {
    //Kullanıcının abonelik tipini bulmak için
    Optional<Subscription> findByUserId(Long userId);
}
