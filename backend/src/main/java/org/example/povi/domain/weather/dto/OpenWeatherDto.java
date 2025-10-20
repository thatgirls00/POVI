package org.example.povi.domain.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenWeatherDto {
    // weather[0].main -> "Clear" / "Clouds" / "Rain" ...
    @JsonProperty("weather")
    private List<Weather> weather;

    // main.temp -> 섭씨(when units=metric)
    @JsonProperty("main")
    private Main main;

    // wind.speed -> m/s
    @JsonProperty("wind")
    private Wind wind;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        @JsonProperty("main")
        private String main;
        @JsonProperty("description")
        private String description;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Main {
        @JsonProperty("temp")
        private Double temp;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wind {
        @JsonProperty("speed")
        private Double speed; // m/s
    }
}