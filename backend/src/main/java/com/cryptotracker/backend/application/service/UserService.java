package com.cryptotracker.backend.application.service;

import com.cryptotracker.backend.application.dto.UserMapper;
import com.cryptotracker.backend.application.dto.request.CreateUserRequest;
import com.cryptotracker.backend.application.dto.request.LoginRequest;
import com.cryptotracker.backend.application.dto.response.AuthResponse;
import com.cryptotracker.backend.application.dto.response.UserResponse;
import com.cryptotracker.backend.domain.model.Portfolio;
import com.cryptotracker.backend.domain.model.User;
import com.cryptotracker.backend.infrastructure.persistence.PortfolioRepository;
import com.cryptotracker.backend.infrastructure.persistence.UserRepository;
import com.cryptotracker.backend.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// @Service: Spring'e "bu sınıf bir iş mantığı bileşenidir, bean olarak kaydet" der.
// Controller bu sınıfı @Autowired veya constructor injection ile kullanacak.
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, PortfolioRepository portfolioRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.portfolioRepository = portfolioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    public UserResponse register(CreateUserRequest request) {
        // Aynı email ile kayıt varsa hata fırlat (DB'de unique constraint var ama
        // önceden kontrol etmek daha anlamlı bir hata mesajı üretir).
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use: " + request.getEmail());
        }

        // Şifreyi asla düz metin saklama — BCrypt ile hash'le.
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        User savedUser = userRepository.save(user);

        // D001: Kayıt olunca otomatik portföy oluştur.
        Portfolio portfolio = new Portfolio();
        portfolio.setUser(savedUser);
        portfolioRepository.save(portfolio);

        return userMapper.toResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        jwtTokenProvider.generateToken(user.getEmail());

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

}