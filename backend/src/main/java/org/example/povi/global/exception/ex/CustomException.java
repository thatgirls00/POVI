package org.example.povi.global.exception.ex;

import lombok.Getter;
import org.example.povi.global.exception.error.ErrorCode;

/**
 * 서비스 전반에서 발생하는 사용자 정의 예외 처리 클래스
 */
@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * ErrorCode를 기반으로 예외 생성
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
