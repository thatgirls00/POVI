package org.example.povi.domain.weather;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openweather")
@Getter @Setter
public class OpenWeatherProperties {
    private String baseUrl;
    private String path;
    private String apiKey;
    private String units = "metric";
    private String exclude = "minutely,hourly,alerts";
}
