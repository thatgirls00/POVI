package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * 이미 가입된 이메일 등 중복 사용자 존재 시 발생하는 예외
 */
public class UserAlreadyExistsException extends CustomException {

    public UserAlreadyExistsException() {
        super(ErrorCode.USER_ALREADY_EXISTS);
    }
}