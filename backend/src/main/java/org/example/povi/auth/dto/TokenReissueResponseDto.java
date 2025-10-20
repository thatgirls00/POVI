package org.example.povi.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * accessToken 재발급 응답 시 사용되는 DTO.
 * 필요 시 refreshToken도 함께 반환됩니다.
 */
@Getter
@AllArgsConstructor
public class TokenReissueResponseDto {

    private String accessToken;
    private String refreshToken;
}