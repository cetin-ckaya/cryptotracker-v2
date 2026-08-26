package com.cryptotracker.backend.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

// @Component: Spring bean olarak kayıt — SecurityConfig ve Filter buraya inject edecek.
@Component
public class JwtTokenProvider {

    // application.properties'teki jwt.secret değerini otomatik inject eder.
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationMs;

    // String key'i HMAC-SHA algoritması için uygun SecretKey'e çevirir.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    // Login başarılı olduğunda çağrılır — kullanıcının email'ini token içine gömer.
    public String generateToken(String email,String role) {
        return Jwts.builder()
                .subject(email)                             // token'ın kime ait olduğu
                .claim("role", role)
                .issuedAt(new Date())                       // oluşturulma zamanı
                .expiration(new Date(System.currentTimeMillis() + expirationMs))  // geçerlilik sonu
                .signWith(getSigningKey())                  // imzala
                .compact();                                 // String'e çevir
    }

    // Her istekte token'dan email'i çıkarmak için kullanılır.
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // Token'ın süresi dolmuş mu veya bozulmuş mu kontrol eder.
    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;  // imza bozuksa veya süre dolmuşsa false döner
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}