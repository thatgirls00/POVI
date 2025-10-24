package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증된 사용자 정보를 응답하는 DTO.
 */
@Schema(description = "인증된 사용자 정보를 담은 응답 DTO")
public record AuthenticatedUserResponseDto(

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 닉네임", example = "홍길동")
        String nickname,

        @Schema(description = "사용자 권한", example = "ROLE_USER")
        String role

) {}