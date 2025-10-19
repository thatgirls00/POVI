package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * - 비밀번호 불일치 시 발생하는 예외
 * - 로그인 시 잘못된 비밀번호 입력에 사용
 */
public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}