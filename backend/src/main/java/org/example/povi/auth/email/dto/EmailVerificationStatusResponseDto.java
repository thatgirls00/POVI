package org.example.povi.auth.email.dto;

/**
 * 이메일 인증 상태 응답 DTO입니다.
 */
public record EmailVerificationStatusResponseDto(

        /**
         * 인증 대상 이메일 주소
         */
        String email,

        /**
         * 이메일 인증 완료 여부
         */
        boolean verified

) {}