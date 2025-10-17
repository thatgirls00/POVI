package org.example.povi.global.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String nickname;   // 선택사항 (원하면 사용자 정보도 반환)
}