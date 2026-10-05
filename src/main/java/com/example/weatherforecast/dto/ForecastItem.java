package com.example.weatherforecast.dto;

public record ForecastItem(
        String date,
        double temperature,
        double minimumTemperature,
        double maximumTemperature,
        String condition,
        String description,
        String icon) {
}
