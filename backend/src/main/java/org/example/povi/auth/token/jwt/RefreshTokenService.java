package org.example.povi.auth.token.jwt;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.dao.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.example.povi.auth.token.entity.RefreshToken;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public void save(String userId, String refreshToken) {
        refreshTokenRepository.save(userId, refreshToken);
    }

    public RefreshToken getByEmail(String userId) {
        String token = refreshTokenRepository.findByUserId(userId);
        return new RefreshToken(userId, token);
    }

    public void delete(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}