package org.example.povi.auth.oauthinfo;

import java.util.Map;

public record KakaoUserInfo(Map<String, Object> attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? id.toString() : null;
    }

    @Override
    public String getProvider() {
        return "KAKAO";
    }

    @Override
    public String getEmail() {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account != null) {
            Object email = account.get("email");
            return email != null ? email.toString() : null;
        }
        return null;
    }

    @Override
    public String getNickname() {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) account.get("profile");
            if (profile != null) {
                Object nickname = profile.get("nickname");
                return nickname != null ? nickname.toString() : null;
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}