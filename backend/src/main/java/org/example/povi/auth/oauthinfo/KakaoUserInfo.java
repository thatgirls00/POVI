package org.example.povi.auth.oauthinfo;

import java.util.Map;

/**
 * Kakao OAuth2 사용자 정보 파싱 클래스
 * (카카오의 nested JSON 구조를 처리)
 */
public class KakaoUserInfo extends OAuth2UserInfo {

    public KakaoUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    /**
     * 카카오 고유 사용자 ID 반환 (루트의 "id" 필드)
     */
    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? id.toString() : null;
    }

    /**
     * OAuth2 제공자명 반환
     */
    @Override
    public String getProvider() {
        return "KAKAO"; // enum 또는 상수화 가능
    }

    /**
     * 사용자 이메일 반환 (kakao_account.email)
     */
    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = getNestedMap(attributes, "kakao_account");
        Object email = kakaoAccount != null ? kakaoAccount.get("email") : null;
        return email != null ? email.toString() : null;
    }

    /**
     * 사용자 닉네임 반환 (kakao_account.profile.nickname)
     */
    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = getNestedMap(attributes, "kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? getNestedMap(kakaoAccount, "profile")
                : null;

        Object nickname = profile != null ? profile.get("nickname") : null;
        return nickname != null ? nickname.toString() : null;
    }

    /**
     * 중첩 맵 안전 추출 메서드 (key가 없거나 형변환 실패 시 null 반환)
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getNestedMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value instanceof Map) ? (Map<String, Object>) value : null;
    }
}