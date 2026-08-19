package com.cryptotracker.backend.infrastructure.persistence;

import com.cryptotracker.backend.domain.model.UserBehaviorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBehaviorEventRepository extends JpaRepository<UserBehaviorEvent,Long> {
}
