package org.example.povi.auth.dto;

/**
 * 인증된 사용자 정보를 응답하는 DTO.
 */
public record AuthenticatedUserResponseDto(
        String email,
        String nickname,
        String role
) {}