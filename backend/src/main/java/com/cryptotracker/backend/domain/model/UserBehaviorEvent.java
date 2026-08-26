package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_behavior_events")
@Getter
@Setter
public class UserBehaviorEvent extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "event_type",nullable = false)
    private String eventType;


    // jsonb: PostgreSQL'e özgü esnek JSON kolon tipi.
    // Event'ten event'e farklı alanlar içerebileceği için
    // sabit kolonlar yerine JSON string olarak tutuyoruz.

    @Column(name = "metadata", columnDefinition = "text")
    private String metadata;
}
