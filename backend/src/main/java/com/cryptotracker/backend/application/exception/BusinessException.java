package com.cryptotracker.backend.application.exception;

//409
public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
}
