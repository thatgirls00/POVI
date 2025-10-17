package org.example.povi.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.povi.auth.dto.*;
import org.example.povi.auth.service.AuthService;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return ResponseEntity.ok(authService.login(requestDto));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponseDto> getMyInfo() {
        CustomJwtUser user = getCurrentUserOrThrow();
        MeResponseDto response = MeResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<TokenReissueResponseDto> reissueAccessToken(
            @RequestBody TokenReissueRequestDto requestDto
    ) {
        TokenReissueResponseDto response = authService.reissueAccessToken(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        CustomJwtUser user = getCurrentUserOrThrow();
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<Void> oauthCallback(@PathVariable String provider,
                                              @RequestParam("accessToken") String accessToken,
                                              @RequestParam("refreshToken") String refreshToken) {
        String redirectUri = "http://localhost:3000/oauth2/redirect"
                + "?accessToken=" + accessToken
                + "&refreshToken=" + refreshToken;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUri)
                .build();
    }

    /**
     * 현재 인증된 사용자의 CustomJwtUser 객체를 반환합니다.
     * 인증되지 않은 경우 401 에러를 반환합니다.
     */
    private CustomJwtUser getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("인증되지 않은 사용자 요청");
            throw new UnauthorizedException();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomJwtUser user) {
            return user;
        }

        log.warn("예상치 못한 Principal 타입: {}", principal.getClass().getName());
        throw new UnauthorizedException();
    }

    private static class UnauthorizedException extends RuntimeException {
    }
}