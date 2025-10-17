package org.example.povi.global.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenReissueResponseDto {
    private String accessToken;
    private String refreshToken;
}
