package org.example.povi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * [정적 리소스 매핑 설정]
 * 업로드된 이미지 파일을 브라우저에서 접근할 수 있도록
 * 로컬 파일 시스템 경로(uploadDir)를 /images/** URL로 매핑
 */

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 요청을 로컬 uploadDir 경로에 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + System.getProperty("user.home") + "/povi-uploads/");
    }
}

