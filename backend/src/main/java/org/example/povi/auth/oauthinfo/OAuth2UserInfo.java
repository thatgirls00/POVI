package org.example.povi.auth.oauthinfo;

import java.util.Map;

/**
 * 소셜 로그인 사용자 정보를 제공하는 추상 클래스.
 * 각 OAuth2 Provider(Google, Kakao 등)별로 상속 구현체를 작성해야 합니다.
 */
public abstract class OAuth2UserInfo {

    /**
     * 소셜 로그인에서 전달된 원시 사용자 정보
     * (ex: Kakao는 kakao_account.profile.nickname 등 중첩 구조)
     */
    protected final Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * 소셜 제공자의 고유 사용자 ID (ex: Kakao의 "id", Google의 "sub")
     */
    public abstract String getProviderId();

    /**
     * 소셜 제공자 이름 (ex: "KAKAO", "GOOGLE")
     */
    public abstract String getProvider();

    /**
     * 사용자 이메일
     */
    public abstract String getEmail();

    /**
     * 사용자 닉네임
     */
    public abstract String getNickname();
}