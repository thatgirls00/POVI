package org.example.povi.global.exception.error;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 예외 발생 시 클라이언트에 반환할 표준 에러 응답 형식
 * - GlobalExceptionHandler에서 반환
 */
@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;

    private ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    /**
     * 정적 팩토리 메서드로 생성
     * @param status HTTP 상태 코드
     * @param error 상태명 (예: BAD_REQUEST)
     * @param message 사용자에게 전달할 상세 메시지
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message);
    }
}