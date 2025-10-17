package org.example.povi.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트가 accessToken + refreshToken을 전달하여
 * accessToken 재발급을 요청할 때 사용하는 DTO.
 */
@Getter
@NoArgsConstructor
public class TokenReissueRequestDto {

    private String accessToken;
    private String refreshToken;
}