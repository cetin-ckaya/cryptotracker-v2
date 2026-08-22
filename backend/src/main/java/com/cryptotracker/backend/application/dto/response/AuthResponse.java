package com.cryptotracker.backend.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Login başarılı olunca dönen tek şey JWT token'dır.
// @AllArgsConstructor: Lombok, tüm alanları alan bir constructor üretir.
// Kullanımı: new AuthResponse(token)
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
}