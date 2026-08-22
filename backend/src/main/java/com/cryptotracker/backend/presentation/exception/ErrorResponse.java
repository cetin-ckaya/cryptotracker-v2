// ErrorResponse.java
package com.cryptotracker.backend.presentation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private LocalDateTime timestamp;
}