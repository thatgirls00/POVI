package org.example.povi.auth.email.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

/**
 * 이메일 인증 메일 템플릿 매핑을 담당
 */
@Component
@RequiredArgsConstructor
public class EmailVerificationTemplateMapper {

    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.verification-link}")
    private String verificationBaseUrl;

    private static final String TEMPLATE_PATH = "mail/verification";
    private static final String SUBJECT = "[POVI] 이메일 인증 요청";

    /**
     * HTML 템플릿 렌더링 처리
     */
    public String renderTemplate(String token) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("verificationLink", verificationBaseUrl + token);

        Context context = new Context();
        context.setVariables(variables);

        return templateEngine.process(TEMPLATE_PATH, context);
    }

    public String getSubject() {
        return SUBJECT;
    }
}