package com.example.weatherforecast.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;

class WeatherServiceTest {
    @Test
    void rejectsOutOfRangeCoordinates() {
        WeatherService service = new WeatherService(RestClient.create(), "test-key");
        assertThrows(IllegalArgumentException.class, () -> service.weatherForCoordinates(91, 0));
    }

    @Test
    void rejectsBlankCity() {
        WeatherService service = new WeatherService(RestClient.create(), "test-key");
        assertThrows(IllegalArgumentException.class, () -> service.weatherForCity(" "));
    }
}
