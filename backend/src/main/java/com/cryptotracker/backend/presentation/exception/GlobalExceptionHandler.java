package com.cryptotracker.backend.presentation.exception;

import com.cryptotracker.backend.application.exception.BusinessException;
import com.cryptotracker.backend.application.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// @RestControllerAdvice: tüm @RestController'lardan fırlayan exception'ları yakalar.
// Buraya düşen her exception için doğru HTTP kodu + JSON body üretiyoruz.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // NotFoundException → 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, ex.getMessage(), LocalDateTime.now()));
    }

    // @Valid başarısız olursa Spring bu exception'ı fırlatır → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // İlk validation hatasının mesajını al
        String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(400, message, LocalDateTime.now()));
    }

    // Genel RuntimeException → 500 (beklenmedik hatalar için güvenlik ağı)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, ex.getMessage(), LocalDateTime.now()));
    }

    // BusinessException → 409 Conflict
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(409, ex.getMessage(), LocalDateTime.now()));
    }
}