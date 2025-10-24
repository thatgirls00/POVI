package org.example.povi.auth.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 이메일 인증 상태 응답 DTO입니다.
 */
@Schema(description = "이메일 인증 상태 응답 DTO")
public record EmailVerificationStatusResponseDto(

        /**
         * 인증 대상 이메일 주소
         */
        @Schema(description = "인증 대상 이메일 주소", example = "user@example.com")
        String email,

        /**
         * 이메일 인증 완료 여부
         */
        @Schema(description = "이메일 인증 완료 여부", example = "true")
        boolean verified

) {}