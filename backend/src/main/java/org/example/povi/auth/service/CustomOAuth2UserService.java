package org.example.povi.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.oauthinfo.CustomOAuth2User;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.entity.UserRole;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2 사용자 정보 불러오기
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 소셜 로그인 플랫폼 구분자 추출 (ex: google, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        AuthProvider provider = parseProvider(registrationId);

        // providerId 추출
        String providerId = extractProviderId(provider, attributes);

        // DB에 해당 사용자 있는지 확인 후 없으면 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> createUser(provider, providerId, attributes));

        return new CustomOAuth2User(
                user.getEmail(),
                provider.name().toLowerCase(),
                providerId,
                user.getNickname(),
                attributes
        );
    }

    private AuthProvider parseProvider(String registrationId) {
        try {
            return AuthProvider.valueOf(registrationId.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인 제공자: " + registrationId);
        }
    }

    private String extractProviderId(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> String.valueOf(attributes.get("id"));
            case GOOGLE -> String.valueOf(attributes.get("sub"));
            default -> throw new OAuth2AuthenticationException("Provider ID 추출 실패: " + provider.name());
        };
    }

    private User createUser(AuthProvider provider, String providerId, Map<String, Object> attributes) {
        String nickname = extractNickname(provider, attributes);
        String email = provider.name().toLowerCase() + "_" + providerId + "@socialuser.com";

        return userRepository.save(User.builder()
                .email(email)
                .nickname(nickname)
                .password("")
                .provider(provider)
                .providerId(providerId)
                .userRole(UserRole.USER)
                .build());
    }

    private String extractNickname(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                yield (String) profile.get("nickname");
            }
            case GOOGLE -> (String) attributes.get("name");
            default -> throw new OAuth2AuthenticationException("닉네임 추출 실패: " + provider.name());
        };
    }
}