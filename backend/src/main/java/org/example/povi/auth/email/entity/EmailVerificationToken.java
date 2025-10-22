package org.example.povi.auth.email.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 이메일 인증 토큰 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    /**
     * 토큰 발급 대상 이메일
     */
    @Column(nullable = false)
    private String email;

    /**
     * 인증용 고유 토큰 (UUID 문자열)
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * 만료 시각 (기준: 발급 후 60분)
     */
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 인증 여부 플래그
     */
    @Builder.Default
    @Column(nullable = false)
    private boolean verified = false;

    /**
     * 토큰 만료 여부 확인
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * 인증 완료 처리
     */
    public void markAsVerified() {
        this.verified = true;
    }

    /**
     * 새 토큰으로 갱신 (재요청 시)
     */
    public void updateToken(String newToken, LocalDateTime newExpiresAt) {
        this.token = newToken;
        this.expiresAt = newExpiresAt;
        this.verified = false;
    }
}