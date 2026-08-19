package com.cryptotracker.backend.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ai_analysis_logs")
@Getter
@Setter
public class AiAnalysisLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "coin_id")
    private Coin coin;


    @Column(name = "analysis_type", nullable = false)
    private String analysisType;

    @Column(name = "recommendation")
    private String recommendation;

    @Column(name = "trend_outlook")
    private String trendOutlook;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
}
