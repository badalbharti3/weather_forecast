package com.example.weatherforecast.controller;

import com.example.weatherforecast.dto.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public WeatherResponse current(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.weatherForCoordinates(lat, lon);
    }

    @GetMapping("/forecast")
    public WeatherResponse forecast(@RequestParam double lat, @RequestParam double lon) {
        return weatherService.weatherForCoordinates(lat, lon);
    }

    @GetMapping("/city")
    public WeatherResponse city(@RequestParam @NotBlank(message = "City name is required") String name) {
        return weatherService.weatherForCity(name);
    }
}
