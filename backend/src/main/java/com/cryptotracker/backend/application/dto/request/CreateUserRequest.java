package com.cryptotracker.backend.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Bu sınıf sadece VERİ TAŞIR — iş mantığı yok, DB bağlantısı yok.
// Kullanıcının kayıt isteğiyle gönderdiği alanları temsil eder.
@Getter
@Setter
public class CreateUserRequest {

    // @NotBlank: null, boş string ("") veya sadece boşluk ("   ")
    // olamaz. Kullanıcı email göndermek ZORUNDA.
    // @Email: Geçerli bir email formatı olmalı (@ işareti, domain vs.)
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    private String email;

    // @Size: Şifre en az 8, en fazla 100 karakter olmalı.
    // Bu kural, validation aşamasında (Controller'a gelmeden önce)
    // otomatik kontrol edilir.
    @NotBlank(message = "Password can not be blank")
    @Size(min = 8,max = 100,message = "Password must be between 8 and 100 characters")
    private String password;

    // fullName opsiyonel - @NotBlank yok, null gelebilir.
    // Kullanıcı kayıt olurken ismini girmek ZORUNDA değil.
    private String fullName;
}
