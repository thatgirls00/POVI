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
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            throw new IllegalStateException("OpenWeather API key가 설정되지 않았습니다.");
        }

        OpenWeatherDto dto = openWeatherWebClient.get()
                .uri(b -> b.path(props.getPath()) // /data/2.5/weather
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", props.getApiKey())
                        .queryParam("units", props.getUnits())  // metric
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class).map(body ->
                                new RuntimeException("OpenWeather error: " + resp.statusCode() + " / " + body)))
                .bodyToMono(OpenWeatherDto.class)
                .block(Duration.ofSeconds(5));

        if (dto == null) throw new IllegalStateException("OpenWeather 응답이 비었습니다.");

        String main = (dto.getWeather() != null && !dto.getWeather().isEmpty() && dto.getWeather().get(0).getMain() != null)
                ? dto.getWeather().get(0).getMain()
                : "Clear";

        Double temp = (dto.getMain() != null) ? dto.getMain().getTemp() : null;
        Double wind = (dto.getWind() != null) ? dto.getWind().getSpeed() : null;

        double tempC = (temp != null) ? temp : Double.NaN;
        double windMs = (wind != null) ? wind : 0.0;

        return new Snapshot(main, tempC, windMs);
    }

    /** 서비스에서 쓰는 최소 요약 */
    public record Snapshot(String weatherMain, double temperatureC, double windMs) {}
}
