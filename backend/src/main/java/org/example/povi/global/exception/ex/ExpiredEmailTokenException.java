package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * 만료된 이메일 인증 토큰 예외
 */
public class ExpiredEmailTokenException extends CustomException {
    public ExpiredEmailTokenException() {
        super(ErrorCode.EMAIL_TOKEN_EXPIRED);
    }
}