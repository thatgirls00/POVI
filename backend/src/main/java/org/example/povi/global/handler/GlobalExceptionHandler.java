package org.example.povi.global.handler;

import org.example.povi.global.exception.ex.AuthorizationException;
import org.example.povi.global.exception.ex.DuplicateTranscriptionException;
import org.example.povi.global.exception.ex.ResourceNotFoundException;
import org.example.povi.global.exception.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * - 전역 예외 처리 핸들러
 *
 * - 모든 컨트롤러에서 발생하는 예외를 한곳에서 처리
 * - 예외 종류에 따라 맞춤 메시지와 HTTP 상태코드를 반환
 * - JSON 기반 ErrorResponse 포맷 사용
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * - IllegalArgumentException 처리
     * - 주로 잘못된 파라미터 또는 내부 로직 오류 시 발생
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
     * - Bean Validation (DTO @Valid) 실패 처리
     * - 유효성 검사를 통과하지 못한 경우 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorResponse error = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "입력값 검증 실패",
                message
        );
        return ResponseEntity.badRequest().body(error);
    }


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

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<String> handleAuthorizationException(AuthorizationException ex) {
        return ResponseEntity  // 403 Forbidden 상태 코드
                .status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }

    /**
     * - 그 외 처리되지 않은 예외 처리
     * - 예상하지 못한 런타임 에러 등
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
}