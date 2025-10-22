package org.example.povi.auth.oauthinfo;

import java.util.Map;

/**
 * Google OAuth2 사용자 정보 파싱 record
 */
public record GoogleUserInfo(Map<String, Object> attributes) implements OAuth2UserInfo {

    @Override
    public String getProviderId() {
        Object sub = attributes.get("sub");
        return sub != null ? sub.toString() : null;
    }

    @Override
    public String getProvider() {
        return "GOOGLE";
    }

    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email != null ? email.toString() : null;
    }

    @Override
    public String getNickname() {
        Object name = attributes.get("name");
        return name != null ? name.toString() : null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}