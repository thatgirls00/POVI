package org.example.povi.auth.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.email.dto.EmailVerificationRequestDto;
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto;
import org.example.povi.auth.email.service.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이메일 인증 관련 요청을 처리하는 컨트롤러입니다.
 *
 * <p>주요 기능:
 *  - 인증 메일 전송 요청
 *  - 토큰 기반 이메일 인증 처리
 *  - 이메일 인증 여부 조회
 */
@RestController
@RequestMapping("/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * 인증 메일을 전송합니다.
     *
     * @param request 이메일 주소 DTO
     * @return 200 OK
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmailVerification(
            @Valid @RequestBody EmailVerificationRequestDto request
    ) {
        emailVerificationService.sendVerificationEmail(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일 인증 토큰을 검증합니다.
     *
     * @param token 이메일 인증 토큰
     * @return 인증 성공 시 200 OK, 실패 시 400 Bad Request
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
            @RequestParam("token") String token
    ) {
        boolean isVerified = emailVerificationService.verifyEmail(token);

        if (isVerified) {
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        }

        return ResponseEntity
                .badRequest()
                .body("유효하지 않거나 만료된 인증 링크입니다.");
    }

    /**
     * 특정 이메일의 인증 여부를 조회합니다.
     *
     * @param email 이메일 주소
     * @return 인증 상태 정보 DTO
     */
    @GetMapping("/status")
    public ResponseEntity<EmailVerificationStatusResponseDto> checkEmailVerificationStatus(
            @RequestParam String email
    ) {
        EmailVerificationStatusResponseDto response =
                emailVerificationService.checkVerificationStatus(email);

        return ResponseEntity.ok(response);
    }
}