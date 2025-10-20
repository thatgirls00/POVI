package org.example.povi.auth.dto;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO.
 */
public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        String nickname
) {}