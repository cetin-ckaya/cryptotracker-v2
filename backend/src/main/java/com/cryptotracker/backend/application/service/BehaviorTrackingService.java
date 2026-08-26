package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.exception.NotFoundException;
import com.cryptotracker.backend.domain.model.UserBehaviorEvent;
import com.cryptotracker.backend.infrastructure.persistence.UserBehaviorEventRepository;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class BehaviorTrackingService {

    private final UserBehaviorEventRepository repository;
    private final UserRepository userRepository;

    public BehaviorTrackingService(UserBehaviorEventRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    // Controller'lardan dogrudan cagirilir: track(email, "GET_PORTFOLIO")
    // createdAt BaseEntity'den otomatik geliyor — elle setlemeye gerek yok
    public void track(String userEmail, String eventType) {
        UserBehaviorEvent event = new UserBehaviorEvent();
        event.setUser(userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found")));
        event.setEventType(eventType);
        repository.save(event);
    }
}