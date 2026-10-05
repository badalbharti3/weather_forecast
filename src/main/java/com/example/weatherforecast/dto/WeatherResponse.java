package com.example.weatherforecast.dto;

import java.util.List;

public record WeatherResponse(
        String city,
        String country,
        CurrentWeather current,
        List<ForecastItem> forecast) {
}
