package org.example.povi.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.dto.*;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.mapper.UserMapper;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.auth.token.jwt.RefreshTokenService;
import org.example.povi.domain.user.entity.User;
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
     * 회원가입 처리
     */
    public void signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new UserAlreadyExistsException();
        }

        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(requestDto.provider().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorCode.INVALID_AUTH_PROVIDER);
        }

        userRepository.findByEmail(requestDto.email()).ifPresent(existingUser -> {
            if (!existingUser.isEmailVerified()) {
                throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
            }
        });

        if (provider == AuthProvider.LOCAL && (requestDto.password() == null || requestDto.password().isBlank())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        User user = UserMapper.toEntity(requestDto, provider, passwordEncoder);
        userRepository.save(user);
    }

    /**
     * 로그인 처리
     */
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        refreshTokenService.save(user.getEmail(), refreshToken);

        return new LoginResponseDto(accessToken, refreshToken, user.getNickname());
    }

    /**
     * 로그아웃 처리
     */
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.delete(user.getEmail());
    }
}