// src/main/java/org/example/povi/domain/weather/WeatherConfig.java
package org.example.povi.domain.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(OpenWeatherProperties.class)
@RequiredArgsConstructor
public class WeatherConfig {
    @Bean
    public WebClient openWeatherWebClient(OpenWeatherProperties props) {

        return WebClient.builder()
                .baseUrl(props.baseUrl())
                .defaultHeaders(h -> h.setAccept(java.util.List.of(MediaType.APPLICATION_JSON)))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(256 * 1024)) // 버퍼 용량(256KB) 조절: 응답 바디가 큰 경우 OutOfMemory 방지.
                        .build())
                .build();
    }
}
