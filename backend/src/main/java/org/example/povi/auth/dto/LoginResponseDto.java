package org.example.povi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO.
 * 액세스 토큰, 리프레시 토큰, 닉네임 포함
 */
@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;
    private String nickname;
}