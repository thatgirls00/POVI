package org.example.povi.auth.token.jwt;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.dao.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(String userId, String refreshToken) {
        refreshTokenRepository.save(userId, refreshToken);
    }

    public String getRefreshToken(String userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public void delete(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}