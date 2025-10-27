package org.example.povi.domain.weather.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.weather.OpenWeatherClient;
import org.example.povi.domain.weather.dto.WeatherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final OpenWeatherClient weatherClient;

    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam double latitude,
            @RequestParam double longitude) {
        try {
            var snapshot = weatherClient.fetchSnapshot(latitude, longitude);
            var response = new WeatherResponse(
                    snapshot.weatherMain(),
                    snapshot.weatherMain().toLowerCase() + " weather",
                    (int) Math.round(snapshot.temperatureC()),
                    (int) Math.round(snapshot.windMs())
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 날씨 정보 가져오기 실패 시 기본값 반환
            var response = new WeatherResponse(
                    "Clear",
                    "clear weather",
                    20,
                    0
            );
            return ResponseEntity.ok(response);
        }
    }
}
