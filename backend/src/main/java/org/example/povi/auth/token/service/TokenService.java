package org.example.povi.auth.token.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.dto.TokenReissueRequestDto;
import org.example.povi.auth.dto.TokenReissueResponseDto;
import org.example.povi.auth.token.entity.RefreshToken;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.auth.token.jwt.RefreshTokenService;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.error.ErrorCode;
import org.example.povi.global.exception.ex.CustomException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    /**
     * Access Token 재발급
     */
    public TokenReissueResponseDto reissueAccessToken(TokenReissueRequestDto requestDto) {
        String refreshToken = requestDto.refreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtTokenProvider.getUserEmail(refreshToken);

        RefreshToken savedToken = refreshTokenService.getByEmail(email);

        if (!refreshToken.equals(savedToken.refreshToken())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());

        return new TokenReissueResponseDto(newAccessToken, refreshToken);
    }
}