package org.example.povi.auth.util;

import lombok.extern.slf4j.Slf4j;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    /**
     * 현재 인증된 사용자 반환 (Authentication 파라미터 활용 X)
     */
    public static CustomJwtUser getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomJwtUser user) {
            return user;
        }

        throw new UnauthorizedException();
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException() {
            super("인증되지 않은 사용자입니다.");
        }
    }
}