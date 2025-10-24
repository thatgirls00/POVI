package org.example.povi.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("데브코스 일취월장 2차프로젝트 API")
                .description("데브코스 일취월장 다이어리 서비스 프로젝트 API 문서")
                .version("1.0");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        // Kakao OAuth2 인증 스킴
        SecurityScheme oauthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Kakao OAuth2 flow for obtaining access token")
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("https://kauth.kakao.com/oauth/authorize")
                                .tokenUrl("https://kauth.kakao.com/oauth/token")
                                .scopes(new Scopes()
                                        .addString("account_email", "이메일 조회 권한")
                                        .addString("profile_nickname", "프로필 닉네임 조회 권한")
                                )
                        )
                );

        // Google OAuth2 인증 스킴
        SecurityScheme oauthScheme2 = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Google OAuth2 flow for obtaining access token")
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("https://accounts.google.com/o/oauth2/v2/auth")
                                .tokenUrl("https://oauth2.googleapis.com/token")
                                .scopes(new Scopes()
                                        .addString("openid", "OpenID Connect scope")
                                        .addString("email", "Read user's email address")
                                        .addString("profile", "Read user's basic profile info")
                                )
                        )
                );



        // 보안 요구사항 설정: JWT 또는 OAuth2
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth")
                .addList("KakaoOAuth")
                .addList("GoogleOAuth");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .schemaRequirement("BearerAuth", securityScheme)
                .schemaRequirement("KakaoOAuth", oauthScheme)
                .schemaRequirement("GoogleOAuth", oauthScheme2);
    }
}
