package org.example.povi.auth.token.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * Redis를 이용한 Refresh Token 저장소 구현체
 * 사용자 ID를 key로 사용하여 Refresh Token을 저장, 조회, 삭제합니다.
 */
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // Refresh Token 유효기간 (7일)
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(1);

    /**
     * Refresh Token 저장
     *
     * @param userId 사용자 이메일 또는 ID
     * @param refreshToken 생성된 리프레시 토큰
     */
    @Override
    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(userId, refreshToken, REFRESH_TOKEN_EXPIRATION);
    }

    /**
     * 저장된 Refresh Token 조회
     *
     * @param userId 사용자 이메일 또는 ID
     * @return 저장된 Refresh Token
     */
    @Override
    public String findByUserId(String userId) {
        return redisTemplate.opsForValue().get(userId);
    }

    /**
     * Refresh Token 삭제
     *
     * @param userId 사용자 이메일 또는 ID
     */
    @Override
    public void deleteByUserId(String userId) {
        redisTemplate.delete(userId);
    }
}