package org.example.povi.auth.mapper;

import org.example.povi.auth.dto.SignupRequestDto;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.entity.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@SuppressWarnings("unchecked")
public class UserMapper {

    /**
     * 일반 회원가입 시 DTO → Entity 매핑
     */
    public static User toEntity(SignupRequestDto dto, AuthProvider provider, PasswordEncoder encoder) {
        return User.builder()
                .email(dto.email())
                .password(provider == AuthProvider.LOCAL ? encoder.encode(dto.password()) : null)
                .nickname(dto.nickname())
                .provider(provider)
                .providerId(dto.providerId())
                .userRole(UserRole.USER)
                .isEmailVerified(false)
                .build();
    }

    /**
     * OAuth 로그인 시 사용자 정보로부터 Entity 생성
     */
    public static User fromOAuth(AuthProvider provider, String providerId, String email, Map<String, Object> attributes) {
        String nickname = extractNickname(provider, attributes);

        return User.builder()
                .email(email)
                .nickname(nickname)
                .password("")
                .provider(provider)
                .providerId(providerId)
                .userRole(UserRole.USER)
                .build();
    }

    /**
     * OAuth Provider에 따라 닉네임 추출
     */
    private static String extractNickname(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                yield (String) profile.get("nickname");
            }
            case GOOGLE -> (String) attributes.get("name");
            default -> "소셜유저";
        };
    }
}