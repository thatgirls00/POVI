package org.example.povi.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 클라이언트가 accessToken과 refreshToken을 전달
 * accessToken 재발급을 요청할 때 사용하는 DTO.
 */
public record TokenReissueRequestDto(

        @NotBlank
        String accessToken,

        @NotBlank
        String refreshToken

) {}