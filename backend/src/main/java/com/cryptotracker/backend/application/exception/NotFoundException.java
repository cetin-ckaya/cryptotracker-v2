// NotFoundException.java
package com.cryptotracker.backend.application.exception;

// 404 döndürmek istediğimizde fırlatırız.
// RuntimeException extend ettik — checked exception değil, try/catch zorunlu değil.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}