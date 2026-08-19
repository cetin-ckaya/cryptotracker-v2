package com.cryptotracker.backend.application.dto;

import com.cryptotracker.backend.application.dto.response.UserResponse;
import com.cryptotracker.backend.domain.model.User;
import org.mapstruct.Mapper;

// componentModel = "spring": MapStruct'ın ürettiği implementasyon sınıfını
// Spring Bean olarak kaydet. Böylece @Autowired veya constructor injection
// ile her yerden kullanabiliriz.
@Mapper(componentModel = "spring")
public interface UserMapper {

    // User entity'sinden UserResponse DTO'suna dönüşüm.
    // MapStruct, aynı isimli alanları (id, email, fullName, createdAt)
    // otomatik eşleştirir. passwordHash gibi UserResponse'da
    // olmayan alanlar otomatik olarak atlanır — güvenlik sağlanmış olur.
    UserResponse toResponse(User user);
}