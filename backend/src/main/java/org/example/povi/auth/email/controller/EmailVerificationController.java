package org.example.povi.auth.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.email.controller.docs.EmailVerificationControllerDocs;
import org.example.povi.auth.email.dto.EmailVerificationRequestDto;
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto;
import org.example.povi.auth.email.service.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 이메일 인증 관련 요청을 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/auth/email")
@RequiredArgsConstructor
public class EmailVerificationController implements EmailVerificationControllerDocs {

    private final EmailVerificationService emailVerificationService;

    /**
     * 인증 메일을 전송
     */
    @PostMapping("/send")
    public ResponseEntity<Void> sendEmailVerification(
            @Valid @RequestBody EmailVerificationRequestDto request
    ) {
        emailVerificationService.sendVerificationEmail(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 이메일 인증 토큰을 검증
     */
    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam("token") String token
    ) {
        boolean isVerified = emailVerificationService.verifyEmail(token);

        if (isVerified) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity
                .badRequest()
                .build();
    }

    /**
     * 이메일 인증 여부를 조회
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