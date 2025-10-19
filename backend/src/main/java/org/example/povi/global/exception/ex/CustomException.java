package org.example.povi.global.exception.ex;

import lombok.Getter;
import org.example.povi.global.exception.error.ErrorCode;

/**
 * 서비스 전반에서 발생하는 사용자 정의 예외 처리 클래스
 * - ErrorCode를 기반으로 상세 예외 메시지 제공
 * - GlobalExceptionHandler에서 처리됨
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode를 기반으로 예외 생성
     * @param errorCode 사전 정의된 예외 코드
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // RuntimeException의 message 필드 설정
        this.errorCode = errorCode;
    }
}