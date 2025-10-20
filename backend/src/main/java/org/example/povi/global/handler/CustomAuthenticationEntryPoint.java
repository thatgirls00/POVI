package org.example.povi.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * - 인증 실패(401 Unauthorized) 시 실행되는 핸들러
 * - 인증되지 않은 사용자가 보호된 리소스에 접근할 때 호출됨
 * - 예: JWT가 없거나, 만료되었거나, 잘못된 형식일 때
 * - SecurityFilterChain 설정에서 .authenticationEntryPoint(...) 로 등록됨
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 실행되는 메서드
     *
     * @param request       HTTP 요청
     * @param response      HTTP 응답
     * @param authException 인증 예외
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // HTTP 상태코드 설정: 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        // JSON 형태의 에러 응답 정의
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("code", 401);
        errorDetails.put("message", "인증이 필요합니다. 토큰이 유효하지 않거나 없습니다.");

        // 응답 출력
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}