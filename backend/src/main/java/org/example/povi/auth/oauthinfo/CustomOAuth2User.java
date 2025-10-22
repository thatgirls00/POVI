package org.example.povi.auth.oauthinfo;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 인증 후 반환되는 사용자 정보를 담는 record
 */
public record CustomOAuth2User(
        String email,
        String provider,
        String providerId,
        String nickname,
        Map<String, Object> attributes
) implements OAuth2User {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return providerId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}