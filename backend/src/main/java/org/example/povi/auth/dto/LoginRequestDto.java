package org.example.povi.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 시 전달되는 이메일과 비밀번호를 담는 DTO.
 */
@Getter
@NoArgsConstructor
public class LoginRequestDto {

    private String email;
    private String password;
}