package org.example.povi.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.mapper.OAuthUserMapper;
import org.example.povi.auth.mapper.UserMapper;
import org.example.povi.auth.oauthinfo.OAuth2UserInfo;
import org.example.povi.auth.oauthinfo.OAuth2UserInfoFactory;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.ex.CustomException;
import org.example.povi.global.exception.error.ErrorCode;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * OAuth2 로그인 성공 시 사용자 정보를 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 로그인 성공 후 호출되는 메서드
     * 사용자 정보 로드 및 저장 처리
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, attributes);

        String providerId = userInfo.getProviderId();
        String email = userInfo.getEmail();
        String provider = userInfo.getProvider();

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (!existingUser.getProvider().equals(provider)) {
                throw new CustomException(ErrorCode.EMAIL_ALREADY_REGISTERED);
            }
        });

        User user = userRepository.findByProviderAndProviderId(AuthProvider.valueOf(provider), providerId)
                .orElseGet(() -> userRepository.save(
                        UserMapper.fromOAuth(AuthProvider.valueOf(provider), providerId, email, attributes)));

        return OAuthUserMapper.toCustomOAuth2User(user, AuthProvider.valueOf(provider), providerId, attributes);
    }

    /**
     * OAuth2 provider 문자열을 enum으로 변환
     */
    private AuthProvider parseProvider(String registrationId) {
        try {
            return AuthProvider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자: " + registrationId);
        }
    }

    /**
     * OAuth2 응답에서 플랫폼 고유 식별자 추출
     */
    private String extractProviderId(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> String.valueOf(attributes.get("id"));
            case GOOGLE -> String.valueOf(attributes.get("sub"));
            default -> throw new OAuth2AuthenticationException("Provider ID 추출 실패: " + provider.name());
        };
    }

    /**
     * OAuth2 응답에서 이메일 주소 추출
     */
    private String extractEmail(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                yield (String) kakaoAccount.get("email");
            }
            case GOOGLE -> (String) attributes.get("email");
            default -> throw new OAuth2AuthenticationException("이메일 추출 실패: " + provider.name());
        };
    }
}