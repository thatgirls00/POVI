package org.example.povi.auth.token.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 사용자별 Refresh Token 정보를 담는 클래스입니다.
 * Redis 등 외부 저장소에 저장될 수 있습니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken implements Serializable {

    private String userId;
    private String refreshToken;
}