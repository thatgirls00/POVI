package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * accessToken 재발급 응답 시 사용되는 DTO.
 * 필요 시 refreshToken도 함께 반환
 */
@Schema(description = "AccessToken 재발급 응답 DTO")
public record TokenReissueResponseDto(

        @Schema(description = "재발급된 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "재발급된 Refresh Token", example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4=")
        String refreshToken

) {}