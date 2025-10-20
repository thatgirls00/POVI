package org.example.povi.auth.dto;

/**
 * accessToken 재발급 응답 시 사용되는 DTO.
 * 필요 시 refreshToken도 함께 반환
 */
public record TokenReissueResponseDto(
        String accessToken,
        String refreshToken
) {}