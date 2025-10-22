package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * 이미 인증이 완료된 이메일에 대한 예외
 */
public class AlreadyVerifiedEmailException extends CustomException {
    public AlreadyVerifiedEmailException() {
        super(ErrorCode.ALREADY_VERIFIED_EMAIL);
    }
}