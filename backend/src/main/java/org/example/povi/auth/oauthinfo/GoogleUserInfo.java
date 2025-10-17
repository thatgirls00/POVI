package org.example.povi.auth.oauthinfo;

import java.util.Map;

/**
 * Google OAuth2 사용자 정보 파싱 클래스
 * (Google의 표준 key 값 기반)
 */
public class GoogleUserInfo extends OAuth2UserInfo {

    public GoogleUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * 구글 사용자 고유 ID (sub 필드 사용)
     */
    @Override
    public String getProviderId() {
        Object sub = attributes.get("sub");
        return sub != null ? sub.toString() : null;
    }

    /**
     * OAuth2 제공자명 반환
     */
    @Override
    public String getProvider() {
        return "GOOGLE"; // enum 또는 상수화 가능
    }

    /**
     * 사용자 이메일 주소 반환
     */
    @Override
    public String getEmail() {
        Object email = attributes.get("email");
        return email != null ? email.toString() : null;
    }

    /**
     * 사용자 닉네임 (이름) 반환
     */
    @Override
    public String getNickname() {
        Object name = attributes.get("name");
        return name != null ? name.toString() : null;
    }
}