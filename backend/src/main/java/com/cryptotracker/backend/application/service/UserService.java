package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.request.CreateUserRequest;
import com.cryptotracker.backend.application.dto.response.UserResponse;
import org.springframework.stereotype.Service;

// @Service: Spring'e "bu sınıf bir iş mantığı bileşenidir, bean olarak kaydet" der.
// Controller bu sınıfı @Autowired veya constructor injection ile kullanacak.
@Service
public class UserService {
    public UserResponse register(CreateUserRequest request) {
        return null;
    }
}
