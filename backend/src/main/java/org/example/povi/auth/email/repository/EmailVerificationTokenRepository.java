package org.example.povi.auth.email.repository;

import org.example.povi.auth.email.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 이메일 인증 토큰 관련 JPA Repository
 */
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    /**
     * 토큰 값으로 인증 토큰 조회
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * 이메일로 인증 토큰 조회
     */
    Optional<EmailVerificationToken> findByEmail(String email);

    /**
     * 토큰 값으로 삭제
     */
    void deleteByToken(String token);

    /**
     * 만료된 이메일 인증 토큰 일괄 삭제
     */
    void deleteAllByExpiresAtBefore(LocalDateTime now);
}