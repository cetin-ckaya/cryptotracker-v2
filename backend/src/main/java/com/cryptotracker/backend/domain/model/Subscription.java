package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription extends BaseEntity{
    @OneToOne
    @JoinColumn(name = "user_id",nullable = false,unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier",nullable = false)
    private SubscriptionTier tier;

    @Column(name = "started_at",nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
