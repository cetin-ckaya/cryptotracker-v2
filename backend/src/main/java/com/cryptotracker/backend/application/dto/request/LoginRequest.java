package com.cryptotracker.backend.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email can not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password can not be blank")
    private String password;
}
