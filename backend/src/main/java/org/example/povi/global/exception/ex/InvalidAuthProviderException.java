package org.example.povi.global.exception;

public class InvalidAuthProviderException extends RuntimeException {
    public InvalidAuthProviderException(String message) {
        super(message);
    }
}