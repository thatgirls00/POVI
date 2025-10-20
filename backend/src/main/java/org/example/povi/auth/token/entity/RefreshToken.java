package org.example.povi.auth.token.entity;

import java.io.Serializable;

/**
 * 사용자별 Refresh Token 정보를 담는 record 클래스
 * Redis 등 외부 저장소에 저장
 */
public record RefreshToken(String userId, String refreshToken) implements Serializable {}