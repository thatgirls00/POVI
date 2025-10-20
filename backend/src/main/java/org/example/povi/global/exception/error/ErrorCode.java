package org.example.povi.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * - 전역 예외 코드 정의 Enum
 * - 서비스 내 공통 예외 상황에 대한 고유 식별자 및 메시지를 관리합니다.
 * - GlobalExceptionHandler 및 CustomException에서 참조합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 / 보안 관련 오류
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다."),
    INVALID_AUTH_PROVIDER("지원하지 않는 인증 제공자입니다."),
    EMAIL_NOT_VERIFIED("이메일 인증이 완료되지 않았습니다."),


    // 사용자 관련 오류
    USER_NOT_FOUND("해당 사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다."),
    INVALID_PASSWORD("비밀번호가 일치하지 않습니다.");

    /**
     * 예외 메시지
     * */
    private final String message;
}
