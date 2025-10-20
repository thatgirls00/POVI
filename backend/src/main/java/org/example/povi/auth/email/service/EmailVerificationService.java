package org.example.povi.auth.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.email.dto.EmailVerificationRequestDto;
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto;
import org.example.povi.auth.email.entity.EmailVerificationToken;
import org.example.povi.auth.email.repository.EmailVerificationTokenRepository;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 이메일 인증 서비스
 * 이메일 인증 요청, 전송, 토큰 검증, 상태 조회 처리
 */
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    private static final long TOKEN_EXPIRATION_MINUTES = 60;

    /**
     * 1. 이메일 인증 요청 처리 (토큰 생성 및 메일 전송)
     */
    @Transactional
    public void sendVerificationEmail(EmailVerificationRequestDto request) {
        String email = request.getEmail();
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);

        EmailVerificationToken tokenEntity = tokenRepository.findByEmail(email)
                .map(existing -> {
                    existing.updateToken(token, expiresAt);
                    return existing;
                })
                .orElse(
                        EmailVerificationToken.builder()
                                .email(email)
                                .token(token)
                                .expiresAt(expiresAt)
                                .verified(false)
                                .build()
                );

        tokenRepository.save(tokenEntity);
        sendEmail(email, token);
    }

    /**
     * 2. 인증 이메일 발송
     */
    private void sendEmail(String toEmail, String token) {
        String verificationLink = "http://localhost:3000/email/verify?token=" + token;
        String subject = "[POVI] 이메일 인증 요청";
        String content = """
                안녕하세요. POVI입니다.

                아래 링크를 클릭하여 이메일 인증을 완료해주세요:

                %s

                ※ 이 링크는 60분 뒤 만료됩니다.
                """.formatted(verificationLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, false); // HTML X
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    /**
     * 3. 인증 링크 클릭 시 토큰 검증
     */
    @Transactional
    public boolean verifyEmail(String token) {
        token = token.trim();

        EmailVerificationToken tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 링크입니다."));

        if (tokenEntity.isExpired()) {
            return false;
        }

        if (tokenEntity.isVerified()) {
            return true;
        }

        tokenEntity.markAsVerified();
        tokenRepository.save(tokenEntity);

        userRepository.findByEmail(tokenEntity.getEmail())
                .ifPresent(user -> {
                    user.verifyEmail();
                    userRepository.save(user);
                });

        return true;
    }

    /**
     * 4. 이메일 인증 상태 확인
     */
    @Transactional(readOnly = true)
    public EmailVerificationStatusResponseDto checkVerificationStatus(String email) {
        boolean isVerified = tokenRepository.findByEmail(email)
                .map(EmailVerificationToken::isVerified)
                .orElse(false);

        return new EmailVerificationStatusResponseDto(email, isVerified);
    }

    /**
     * 토큰 문자열 생성
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}