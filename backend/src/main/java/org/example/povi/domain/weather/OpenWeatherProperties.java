package org.example.povi.domain.weather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweather")
public record OpenWeatherProperties(
        String baseUrl,
        String path,
        String apiKey,
        String units,
        String exclude
) {
    public OpenWeatherProperties {
        // 기본값 설정
        if (units == null) units = "metric";
        if (exclude == null) exclude = "minutely,hourly,alerts";
    }
}
