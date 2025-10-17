package org.example.povi.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateTranscriptionException.class)
    public ResponseEntity<String> handleDuplicateTranscription(DuplicateTranscriptionException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict 상태 코드
                .body(ex.getMessage()); // "이미 필사한 명언입니다." 메시지
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 Not Found 상태 코드
                .body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400 Bad Request 상태 코드
                .body(ex.getMessage());
    }
}