package org.example.povi.auth.token.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * JWT 인증 후 SecurityContext 에 저장되는 사용자 정보 클래스.
 * UserDetails 구현체로 Spring Security 인증 객체로 사용됩니다.
 */
@Getter
@AllArgsConstructor
public class CustomJwtUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickname;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 기본 사용자 권한 부여
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 패스워드는 사용하지 않음 (JWT 인증 기반)
        return null;
    }

    @Override
    public String getUsername() {
        // UserDetails 식별자로 사용됨 (보통 email 또는 username)
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안 함
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 안 함
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 안 함
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 상태
    }
}