package com.example.weatherforecast.service;

import com.example.weatherforecast.dto.CurrentWeather;
import com.example.weatherforecast.dto.ForecastItem;
import com.example.weatherforecast.dto.LocationResponse;
import com.example.weatherforecast.dto.WeatherResponse;
import com.example.weatherforecast.exception.CityNotFoundException;
import com.example.weatherforecast.exception.WeatherApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMM d").withZone(ZoneOffset.UTC);
    private final RestClient client;
    private final String apiKey;

    public WeatherService(RestClient weatherRestClient, @Value("${weather.api.key}") String apiKey) {
        this.client = weatherRestClient;
        this.apiKey = apiKey;
    }

    public WeatherResponse weatherForCoordinates(double latitude, double longitude) {
        validateCoordinates(latitude, longitude);
        JsonNode current = get("/data/2.5/weather", Map.of(
                "lat", latitude, "lon", longitude, "units", "metric"));
        JsonNode forecast = get("/data/2.5/forecast", Map.of(
                "lat", latitude, "lon", longitude, "units", "metric"));
        return toResponse(current, forecast);
    }

    public WeatherResponse weatherForCity(String city) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City name is required");
        }
        JsonNode location = get("/geo/1.0/direct", Map.of("q", city.trim(), "limit", 1));
        if (!location.isArray() || location.isEmpty()) {
            throw new CityNotFoundException(city.trim());
        }
        JsonNode place = location.get(0);
        WeatherResponse weather = weatherForCoordinates(place.path("lat").asDouble(), place.path("lon").asDouble());
        String country = place.path("country").asText(weather.country());
        return new WeatherResponse(place.path("name").asText(weather.city()), country, weather.current(), weather.forecast());
    }

    private JsonNode get(String path, Map<String, ?> parameters) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new WeatherApiException("Weather API key is not configured");
        }
        Map<String, Object> query = new HashMap<>(parameters);
        query.put("appid", apiKey);
        try {
            return client.get().uri(uriBuilder -> {
                        var builder = uriBuilder.path(path);
                        query.forEach(builder::queryParam);
                        return builder.build();
                    })
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (RestClientException ex) {
            throw new WeatherApiException("Weather provider request failed", ex);
        }
    }

    private WeatherResponse toResponse(JsonNode current, JsonNode forecast) {
        JsonNode main = current.path("main");
        JsonNode wind = current.path("wind");
        JsonNode condition = current.path("weather").path(0);
        CurrentWeather now = new CurrentWeather(
                main.path("temp").asDouble(),
                main.path("feels_like").asDouble(),
                condition.path("main").asText("Unknown"),
                condition.path("description").asText("Unavailable"),
                condition.path("icon").asText("01d"),
                main.path("humidity").asInt(),
                wind.path("speed").asDouble(),
                main.path("pressure").asInt(),
                current.path("visibility").asInt(),
                current.path("sys").path("sunrise").asLong(),
                current.path("sys").path("sunset").asLong());

        List<ForecastItem> items = new ArrayList<>();
        Map<String, JsonNode> daily = new java.util.LinkedHashMap<>();
        for (JsonNode item : forecast.path("list")) {
            String dateTime = item.path("dt_txt").asText("");
            if (dateTime.length() < 10) {
                continue;
            }
            String date = dateTime.substring(0, 10);
            daily.putIfAbsent(date, item);
        }
        daily.values().stream().limit(5).forEach(item -> {
            JsonNode itemMain = item.path("main");
            JsonNode itemWeather = item.path("weather").path(0);
            items.add(new ForecastItem(
                    DATE_FORMAT.format(Instant.ofEpochSecond(item.path("dt").asLong())),
                    itemMain.path("temp").asDouble(),
                    itemMain.path("temp_min").asDouble(),
                    itemMain.path("temp_max").asDouble(),
                    itemWeather.path("main").asText("Unknown"),
                    itemWeather.path("description").asText("Unavailable"),
                    itemWeather.path("icon").asText("01d")));
        });
        return new WeatherResponse(
                current.path("name").asText("Unknown"),
                current.path("sys").path("country").asText(""),
                now, items);
    }

    private void validateCoordinates(double latitude, double longitude) {
        if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Latitude or longitude is invalid");
        }
    }
}
