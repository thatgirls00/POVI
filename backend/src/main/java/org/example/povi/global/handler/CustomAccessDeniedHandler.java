package org.example.povi.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * - 인가 실패(403 Forbidden) 시 실행되는 핸들러
 * - 인증은 되었으나 권한이 없는 경우 (예: ROLE_ADMIN만 접근 가능한 자원에 USER 접근 등)
 * - SecurityFilterChain 설정에서 .accessDeniedHandler(...) 로 등록됨
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 사용자가 적절한 권한 없이 보호된 리소스에 접근했을 때 호출됩니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param accessDeniedException 발생한 예외 객체
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // 로그 기록 (권한 부족 사유)
        log.warn("권한 부족: {}", accessDeniedException.getMessage());

        // HTTP 상태코드 설정: 403 Forbidden
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // 사용자에게 반환할 에러 메시지 JSON 형식
        response.getWriter().write("{\"message\": \"권한이 없습니다.\", \"code\": 403}");
    }
}