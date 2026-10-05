package com.example.weatherforecast.dto;

public record CurrentWeather(
        double temperature,
        double feelsLike,
        String condition,
        String description,
        String icon,
        int humidity,
        double windSpeed,
        int pressure,
        int visibility,
        long sunrise,
        long sunset) {
}
