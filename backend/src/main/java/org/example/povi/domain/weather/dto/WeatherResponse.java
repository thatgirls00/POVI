package org.example.povi.domain.weather.dto;

public record WeatherResponse(
        String main,
        String description,
        int temp,
        int windSpeed
) {}
