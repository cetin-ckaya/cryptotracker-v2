package com.cryptotracker.backend.presentation.controller;

import com.cryptotracker.backend.application.dto.request.CreateUserRequest;
import com.cryptotracker.backend.application.dto.request.LoginRequest;
import com.cryptotracker.backend.application.dto.response.AuthResponse;
import com.cryptotracker.backend.application.dto.response.UserResponse;
import com.cryptotracker.backend.application.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RestController = @Controller + @ResponseBody
// Metodların return değeri otomatik olarak JSON'a çevrilip response body'ye yazılır.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    // Constructor injection — field injection (@Autowired) yerine bu tercih edilir.
    // Nedeni: test yazarken mock nesnesini kolayca constructor'dan geçirebilirsin.
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    // @Valid: Spring'e "request body'yi validate et" der.
    // CreateUserRequest içindeki @NotBlank, @Email gibi kurallar burada devreye girer.
    // Validation başarısız olursa controller metoduna hiç girmeden 400 Bad Request döner.
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid CreateUserRequest request) {
        UserResponse response = userService.register(request);
        // 201 Created: yeni kaynak oluşturuldu — POST'un standart başarı kodu budur.
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
