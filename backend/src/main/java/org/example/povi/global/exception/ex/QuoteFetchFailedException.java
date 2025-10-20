package org.example.povi.global.exception.ex;

public class QuoteFetchFailedException extends RuntimeException {
    public QuoteFetchFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}