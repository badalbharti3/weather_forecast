package com.example.weatherforecast.controller;

import com.example.weatherforecast.dto.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {
    @Autowired MockMvc mvc;
    @MockBean WeatherService service;

    @Test
    void forwardsCoordinateRequest() throws Exception {
        when(service.weatherForCoordinates(28.6, 77.2)).thenReturn(new WeatherResponse("Test", "TS", null, java.util.List.of()));
        mvc.perform(get("/api/weather/current").param("lat", "28.6").param("lon", "77.2"))
                .andExpect(status().isOk());
    }

    @Test
    void acceptsCitySearch() throws Exception {
        when(service.weatherForCity("Delhi")).thenReturn(new WeatherResponse("Delhi", "IN", null, java.util.List.of()));
        mvc.perform(get("/api/weather/city").param("name", "Delhi")).andExpect(status().isOk());
    }
}
