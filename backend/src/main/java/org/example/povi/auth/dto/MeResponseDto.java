package org.example.povi.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 로그인한 사용자의 정보를 반환하는 응답 DTO.
 */
@Getter
@Builder
public class MeResponseDto {

    private final Long id;
    private final String email;
    private final String nickname;
}