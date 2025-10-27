package org.example.povi.global.handler;

import org.example.povi.global.exception.error.ErrorCode;
import org.example.povi.global.exception.ex.AuthorizationException;
import org.example.povi.global.exception.ex.DuplicateTranscriptionException;
import org.example.povi.global.exception.ex.ResourceNotFoundException;
import org.example.povi.global.exception.error.ErrorResponse;
import org.example.povi.global.exception.ex.CustomException;
import org.example.povi.global.exception.ex.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * - 전역 예외 처리 핸들러
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * - IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "잘못된 요청입니다.",
                ex.getMessage()
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * - Bean Validation 실패 시 처리 (예: @NotBlank 등)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST", // 프론트 고정 코드로 판단 가능
                "요청 형식이 올바르지 않습니다." // 클라이언트에 보여줄 메시지
        );
        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(DuplicateTranscriptionException.class)
    public ResponseEntity<String> handleDuplicateTranscription(DuplicateTranscriptionException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict 상태 코드
                .body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404 Not Found 상태 코드
                .body(ex.getMessage());
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleAuthorizationException(AuthorizationException ex) {
        return ResponseEntity  // 403 Forbidden 상태 코드
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    /**
     * - 그 외 처리되지 않은 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "서버 내부 오류",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * - 인증되지 않은 사용자 요청 예외 처리
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException e) {
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.UNAUTHORIZED.value(),
                e.getErrorCode().name(),
                e.getErrorCode().getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    /**
     * - 프로젝트 내에서 사용하는 CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "요청 처리 실패",
                code.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}