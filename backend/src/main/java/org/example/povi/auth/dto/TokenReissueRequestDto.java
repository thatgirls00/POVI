package org.example.povi.global.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenReissueRequestDto {
    private String refreshToken;
}