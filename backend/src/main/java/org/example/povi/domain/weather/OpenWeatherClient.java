package org.example.povi.domain.weather;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.weather.dto.OpenWeatherDto;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OpenWeatherClient {

    private final WebClient openWeatherWebClient;
    private final OpenWeatherProperties props;

    public Snapshot fetchSnapshot(double lat, double lon) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw new IllegalStateException("OpenWeather API key가 설정되지 않았습니다.");
        }

        OpenWeatherDto dto = openWeatherWebClient.get()
                .uri(b -> b.path(props.path()) // /data/2.5/weather
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", props.apiKey())
                        .queryParam("units", props.units())  // metric
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new RuntimeException("OpenWeather error: " + resp.statusCode() + " / " + body)))
                .bodyToMono(OpenWeatherDto.class)
                .block(Duration.ofSeconds(5));

        if (dto == null) throw new IllegalStateException("OpenWeather 응답이 비었습니다.");

        String main = extractWeatherMain(dto);
        double tempC = extractTemperature(dto);
        double windMs = extractWindSpeed(dto);

        return new Snapshot(main, tempC, windMs);
    }

    /** 서비스에서 쓰는 최소 요약 */
    public record Snapshot(String weatherMain, double temperatureC, double windMs) {}
    
    private String extractWeatherMain(OpenWeatherDto dto) {
        return dto.weather() != null && !dto.weather().isEmpty() && dto.weather().get(0).main() != null
                ? dto.weather().get(0).main()
                : "Clear";
    }
    
    private double extractTemperature(OpenWeatherDto dto) {
        return dto.main() != null && dto.main().temp() != null
                ? dto.main().temp()
                : Double.NaN;
    }
    
    private double extractWindSpeed(OpenWeatherDto dto) {
        return dto.wind() != null && dto.wind().speed() != null
                ? dto.wind().speed()
                : 0.0;
    }
}
