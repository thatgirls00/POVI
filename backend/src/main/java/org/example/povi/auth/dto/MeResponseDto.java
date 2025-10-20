package org.example.povi.auth.dto;

import org.example.povi.auth.token.jwt.CustomJwtUser;

/**
 * 로그인한 사용자의 정보를 반환하는 응답 DTO.
 */
public record MeResponseDto(
        Long id,
        String email,
        String nickname
) {
    /**
     * CustomJwtUser 객체를 기반으로 MeResponseDto를 생성
     */
    public static MeResponseDto from(CustomJwtUser user) {
        return new MeResponseDto(
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}