package org.example.povi.global.exception.ex;

import org.example.povi.global.exception.error.ErrorCode;

/**
 * 사용자 조회 실패 시 발생하는 예외
 * - 이메일, ID 기반으로 사용자를 찾지 못했을 때 사용
 */
public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}