package org.example.povi.auth.oauthinfo;

import org.example.povi.auth.enums.AuthProvider;

import java.util.Map;

/**
 * OAuth2UserInfo 구현체를 provider별로 반환해주는 Factory 클래스
 */
public class OAuth2UserInfoFactory {

    /**
     * 소셜 제공자(provider)에 따라 적절한 OAuth2UserInfo 인스턴스를 반환

     */
    public static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> attributes) {
        AuthProvider authProvider;
        try {
            authProvider = AuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 소셜 로그인 provider: " + provider);
        }

        return switch (authProvider) {
            case KAKAO -> new KakaoUserInfo(attributes);
            case GOOGLE -> new GoogleUserInfo(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 provider: " + authProvider);
        };
    }
}