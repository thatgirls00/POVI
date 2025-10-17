package org.example.povi.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.dto.*;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.auth.token.jwt.RefreshTokenService;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.entity.UserRole;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.error.ErrorCode;
import org.example.povi.global.exception.ex.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 회원가입 (이메일 인증 여부 포함)
     */
    public void signup(SignupRequestDto requestDto) {
        // 이미 가입된 이메일인지 확인
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        // AuthProvider 값 검증
        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(requestDto.getProvider().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidAuthProviderException();
        }

        // 이메일 인증 여부 확인 (email_verification_token 테이블 또는 user 테이블에서)
        // 만약 이메일 인증을 완료하지 않았다면 회원가입 차단
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.isEmailVerified()) {
                        throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED); // ⚠이메일 미인증 예외
                    }
                });

        // User 엔티티 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getNickname())
                .provider(provider)
                .providerId(requestDto.getProviderId())
                .userRole(UserRole.USER)
                .isEmailVerified(false) // ⚠기본값 false로 설정
                .build();

        // DB 저장
        userRepository.save(user);
    }

    /**
     * 로그인 (이메일 인증 확인 포함)
     */
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(UserNotFoundException::new);

        // 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        // 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // Refresh Token 저장
        refreshTokenService.save(user.getEmail(), refreshToken);

        // 응답 DTO 반환
        return new LoginResponseDto(accessToken, refreshToken, user.getNickname());
    }

    /**
     * 로그인된 사용자 정보 조회
     */
    public MeResponseDto getCurrentUserInfo(User user) {
        return MeResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }

    /**
     * 로그아웃 (Refresh Token 제거)
     */
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.delete(user.getEmail());
    }

    /**
     * Access Token 재발급
     */
    public TokenReissueResponseDto reissueAccessToken(TokenReissueRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        // Refresh Token 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 사용자 이메일 추출
        String email = jwtTokenProvider.getUserEmail(refreshToken);

        // Redis에 저장된 Refresh Token과 비교
        String storedRefreshToken = refreshTokenService.getRefreshToken(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 사용자 존재 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 새로운 Access Token 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        return new TokenReissueResponseDto(newAccessToken, refreshToken);
    }
}