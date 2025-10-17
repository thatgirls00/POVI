package org.example.povi.auth.oauthinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 인증 후 반환되는 사용자 정보를 담는 클래스
 * (Spring Security의 OAuth2User 구현체)
 */
@Getter
@AllArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    /**
     * 사용자 이메일
     */
    private final String email;

    /**
     * 소셜 로그인 제공자명 (예: kakao, google)
     */
    private final String provider;

    /**
     * 제공자별 고유 식별자 (ex. 카카오 ID, 구글 sub ID)
     */
    private final String providerId;

    /**
     * 사용자 닉네임
     */
    private final String nickname;

    /**
     * OAuth2에서 제공하는 원본 attributes
     */
    private final Map<String, Object> attributes;

    /**
     * 사용자의 권한 목록 반환
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 기본적으로 USER 권한 부여
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * 사용자 고유 식별자 반환 (providerId 기준)
     */
    @Override
    public String getName() {
        return providerId;
    }

    /**
     * OAuth2 Provider로부터 전달받은 attributes 반환
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}