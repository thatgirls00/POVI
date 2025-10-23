package org.example.povi.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherDto(
        // weather[0].main -> "Clear" / "Clouds" / "Rain" ...
        @JsonProperty("weather") List<Weather> weather,
        
        // main.temp -> 섭씨(when units=metric)
        @JsonProperty("main") Main main,
        
        // wind.speed -> m/s
        @JsonProperty("wind") Wind wind
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Weather(
            @JsonProperty("main") String main,
            @JsonProperty("description") String description
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Main(
            @JsonProperty("temp") Double temp
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(
            @JsonProperty("speed") Double speed // m/s
    ) {}
}