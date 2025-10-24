package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.auth.token.jwt.CustomJwtUser;

/**
 * 로그인한 사용자의 정보를 반환하는 응답 DTO.
 */
@Schema(description = "로그인한 사용자의 정보를 담는 응답 DTO")
public record MeResponseDto(

        @Schema(description = "사용자 ID", example = "1")
        Long id,

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "홍길동")
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