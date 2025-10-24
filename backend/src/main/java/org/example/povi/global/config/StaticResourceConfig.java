package org.example.povi.global.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload.diary.dir}")
    private String diaryDir;

    @Value("${file.upload.community.dir}")
    private String communityDir;

    @Value("${file.upload.profile.dir}")
    private String profileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 다이어리 이미지
        registry.addResourceHandler("/images/diary/**")
                .addResourceLocations("file:" + diaryDir + "/");

        // 커뮤니티 이미지
        registry.addResourceHandler("/images/community/**")
                .addResourceLocations("file:" + communityDir + "/");

        // 프로필 이미지
        registry.addResourceHandler("/images/profile/**")
                .addResourceLocations("file:" + profileDir + "/");
    }
}
