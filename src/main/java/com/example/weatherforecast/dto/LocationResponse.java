package com.example.weatherforecast.dto;

public record LocationResponse(double latitude, double longitude, String city, String country) {
}
