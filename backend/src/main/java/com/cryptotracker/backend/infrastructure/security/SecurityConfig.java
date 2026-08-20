package com.cryptotracker.backend.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

// GEÇİCİ — Gün 20-23'te JWT ile değiştirilecek.
// Şimdilik tüm endpoint'lere izin veriyoruz ki Postman'de test edebilelim.
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF: tarayıcı tabanlı saldırılara karşı koruma.
                // REST API'lerde JWT kullandığımız için CSRF'e gerek yok — kapatıyoruz.
                .csrf(AbstractHttpConfigurer::disable)
                // Şimdilik tüm isteklere izin ver — JWT gelince burayı kısıtlayacağız.
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}