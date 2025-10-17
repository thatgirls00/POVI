package org.example.povi.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청을 위한 DTO.
 */
@Getter
@NoArgsConstructor
public class SignupRequestDto {

    private String email;
    private String password;
    private String nickname;
    private String provider;      // ex) kakao, google
    private String providerId;    // 소셜 플랫폼의 유저 고유 ID
}