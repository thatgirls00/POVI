package org.example.povi.global.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    private String email;
    private String password;
    private String nickname;
    private String provider;
    private String providerId;
}