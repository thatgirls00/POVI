package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 클라이언트가 accessToken과 refreshToken을 전달하여
 * accessToken 재발급을 요청할 때 사용하는 DTO.
 */
@Schema(description = "AccessToken 재발급 요청 DTO")
public record TokenReissueRequestDto(

        @Schema(description = "만료되었거나 만료 예정인 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        @NotBlank(message = "Access Token은 필수입니다.")
        String accessToken,

        @Schema(description = "유효한 Refresh Token", example = "dGhpcyBpcyBhIHZhbGlkIHJlZnJlc2ggdG9rZW4=")
        @NotBlank(message = "Refresh Token은 필수입니다.")
        String refreshToken

) {}