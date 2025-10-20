package org.example.povi.auth.email.mapper;

import org.example.povi.auth.email.entity.EmailVerificationToken;

import java.time.LocalDateTime;

public class EmailVerificationTokenMapper {

    /**
     * 기존 토큰이 있으면 갱신, 없으면 새로 생성
     */
    public static EmailVerificationToken createOrUpdate(
            EmailVerificationToken existingToken,
            String email,
            String token,
            LocalDateTime expiresAt
    ) {
        if (existingToken != null) {
            existingToken.updateToken(token, expiresAt);
            return existingToken;
        }

        return EmailVerificationToken.builder()
                .email(email)
                .token(token)
                .expiresAt(expiresAt)
                .verified(false)
                .build();
    }
}