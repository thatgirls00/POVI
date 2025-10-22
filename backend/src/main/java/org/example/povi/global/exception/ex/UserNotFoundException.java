package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * 사용자 조회 실패 시 발생하는 예외
 */
public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}