package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그인 성공 시 클라이언트에 전달되는 응답 DTO.
 */
@Schema(description = "로그인 성공 시 클라이언트에 전달되는 응답 DTO")
public record LoginResponseDto(

        @Schema(description = "Access Token (JWT)", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0...")
        String accessToken,

        @Schema(description = "Refresh Token (JWT)", example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2...")
        String refreshToken,

        @Schema(description = "사용자 닉네임", example = "홍길동")
        String nickname

) {}