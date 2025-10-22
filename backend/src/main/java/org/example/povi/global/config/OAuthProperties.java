package org.example.povi.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuthProperties {
    private String redirectUri;

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}