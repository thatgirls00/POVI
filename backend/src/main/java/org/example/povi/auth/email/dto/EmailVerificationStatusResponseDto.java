package org.example.povi.auth.email.dto;

import lombok.*;

/**
 * 이메일 인증 상태 응답 DTO입니다.
 *
 * <p>특정 이메일의 인증 여부를 반환합니다.</p>
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailVerificationStatusResponseDto {

    /**
     * 인증 대상 이메일 주소
     */
    private String email;

    /**
     * 이메일 인증 완료 여부
     */
    private boolean verified;
}