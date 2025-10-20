package org.example.povi.auth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.oauthinfo.CustomOAuth2User;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.entity.UserRole;
import org.example.povi.domain.user.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String FRONTEND_REDIRECT_URL = "http://localhost:3000/oauth2/redirect";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        try {
            User user = findOrRegisterUser(oAuth2User);
            String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
            String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

            String redirectUrl = buildRedirectUrl(accessToken, refreshToken);
            response.sendRedirect(redirectUrl);

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "OAuth2 로그인 처리 실패: 지원하지 않는 provider");
        }
    }

    /**
     * 기존 회원 조회 또는 자동 회원가입 처리
     */
    private User findOrRegisterUser(CustomOAuth2User oAuth2User) {
        AuthProvider authProvider = parseAuthProvider(oAuth2User.getProvider());
        String providerId = oAuth2User.getProviderId();

        return userRepository.findByProviderAndProviderId(authProvider, providerId)
                .orElseGet(() -> registerNewUser(oAuth2User, authProvider));
    }

    /**
     * 신규 회원 자동 가입
     */
    private User registerNewUser(CustomOAuth2User oAuth2User, AuthProvider provider) {
        return userRepository.save(User.builder()
                .email(oAuth2User.getEmail())
                .nickname(oAuth2User.getNickname())
                .password("")
                .provider(provider)
                .providerId(oAuth2User.getProviderId())
                .userRole(UserRole.USER)
                .build());
    }

    /**
     * 리다이렉트 URL 생성 (accessToken, refreshToken 포함)
     */
    private String buildRedirectUrl(String accessToken, String refreshToken) {
        return FRONTEND_REDIRECT_URL +
                "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8) +
                "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);
    }

    /**
     * 문자열로부터 AuthProvider enum 파싱
     */
    private AuthProvider parseAuthProvider(String provider) {
        try {
            return AuthProvider.valueOf(provider.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Auth Provider: " + provider);
        }
    }
}