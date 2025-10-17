package org.example.povi.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 인증된 사용자 정보를 응답하는 DTO.
 */
@Getter
@Builder
public class AuthenticatedUserResponseDto {

    private final String email;
    private final String nickname;
    private final String role;
}