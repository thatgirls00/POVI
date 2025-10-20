package org.example.povi.auth.email.repository;

import org.example.povi.auth.email.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 이메일 인증 토큰 관련 JPA Repository
 * <p>
 * 토큰으로 조회, 이메일로 조회, 토큰 삭제 기능 제공
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
}