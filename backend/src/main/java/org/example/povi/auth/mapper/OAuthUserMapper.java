package org.example.povi.auth.mapper;

import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.oauthinfo.CustomOAuth2User;
import org.example.povi.domain.user.entity.User;

import java.util.Map;

public class OAuthUserMapper {

    public static CustomOAuth2User toCustomOAuth2User(User user, AuthProvider provider, String providerId, Map<String, Object> attributes) {
        return new CustomOAuth2User(
                user.getEmail(),
                provider.name().toLowerCase(),
                providerId,
                user.getNickname(),
                attributes
        );
    }
}