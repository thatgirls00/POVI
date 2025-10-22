package org.example.povi.auth.oauthinfo;

import java.util.Map;

/**
 * 소셜 로그인 사용자 정보를 제공하는 인터페이스
 */
public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmail();
    String getNickname();
    Map<String, Object> getAttributes();
}