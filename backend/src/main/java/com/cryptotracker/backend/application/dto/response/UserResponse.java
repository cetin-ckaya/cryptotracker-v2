package com.cryptotracker.backend.application.dto.response;
//Kullanıcıya dönecek bilgiler

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse {
    //Response DTO'larda @NotBlank gibi validasyon anotasyonları kullanılmaz — bu veriyi biz üretip gönderiyoruz, kullanıcıdan gelmiyor.
    private Long id;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
}
