package org.example.povi.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.dto.*;
import org.example.povi.auth.service.AuthService;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.auth.token.service.TokenService;
import org.example.povi.global.config.OAuthProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.example.povi.auth.util.SecurityUtil.getCurrentUserOrThrow;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

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
        return ResponseEntity.ok(MeResponseDto.from(user));
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<TokenReissueResponseDto> reissueAccessToken(
            @Valid @RequestBody TokenReissueRequestDto requestDto
    ) {
        TokenReissueResponseDto response = tokenService.reissueAccessToken(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        CustomJwtUser user = getCurrentUserOrThrow();
        authService.logout(user.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<TokenReissueResponseDto> oauthCallback(@PathVariable String provider,
                                                                 @RequestParam("accessToken") String accessToken,
                                                                 @RequestParam("refreshToken") String refreshToken) {
        TokenReissueResponseDto response = new TokenReissueResponseDto(accessToken, refreshToken);
        return ResponseEntity.ok(response);
    }
}