package org.example.povi.auth.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.email.limiter.EmailVerificationRateLimiter;
import org.example.povi.auth.email.dto.EmailVerificationRequestDto;
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto;
import org.example.povi.auth.email.entity.EmailVerificationToken;
import org.example.povi.auth.email.mapper.EmailVerificationTokenMapper;
import org.example.povi.auth.email.mapper.EmailVerificationTemplateMapper;
import org.example.povi.auth.email.repository.EmailVerificationTokenRepository;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.ex.AlreadyVerifiedEmailException;
import org.example.povi.global.exception.ex.ExpiredEmailTokenException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.povi.global.exception.ex.InvalidEmailTokenException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final EmailVerificationTemplateMapper templateMapper;
    private final EmailVerificationRateLimiter rateLimiter;

    private static final long TOKEN_EXPIRATION_MINUTES = 60;

    /**
     * 이메일 인증 요청 처리
     */
    @Transactional
    public void sendVerificationEmail(EmailVerificationRequestDto request) {
        String email = request.email();
        rateLimiter.validateSendLimit(email);
        tokenRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());

        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_MINUTES);
        EmailVerificationToken existing = tokenRepository.findByEmail(email).orElse(null);

        if (existing != null) {
            if (existing.isVerified()) {
                throw new AlreadyVerifiedEmailException();
            }
            if (existing.isExpired()) {
                tokenRepository.delete(existing);
                existing = null;
            }
        }

        EmailVerificationToken tokenEntity = EmailVerificationTokenMapper.createOrUpdate(existing, email, token, expiresAt);
        tokenRepository.save(tokenEntity);
        sendEmail(email, token);
    }

    /**
     * 인증 이메일 발송
     */
    private void sendEmail(String toEmail, String token) {
        String subject = templateMapper.getSubject();
        String htmlContent = templateMapper.renderTemplate(token);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    /**
     * 인증 링크 클릭 시 토큰 검증
     */
    @Transactional
    public boolean verifyEmail(String token) {
        EmailVerificationToken tokenEntity = tokenRepository.findByToken(token.trim())
                .orElseThrow(InvalidEmailTokenException::new);

        if (tokenEntity.isExpired()) {
            throw new ExpiredEmailTokenException();
        }

        if (tokenEntity.isVerified()) {
            throw new AlreadyVerifiedEmailException();
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
     * 이메일 인증 상태 확인
     */
    @Transactional(readOnly = true)
    public EmailVerificationStatusResponseDto checkVerificationStatus(String email) {
        boolean isVerified = tokenRepository.findByEmail(email)
                .map(EmailVerificationToken::isVerified)
                .orElse(false);

        return new EmailVerificationStatusResponseDto(email, isVerified);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

}