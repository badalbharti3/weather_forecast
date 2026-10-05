package com.example.weatherforecast.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorResponse(int status, String message, Instant timestamp) {
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CityNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotFound(CityNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({WeatherApiException.class, RestClientResponseException.class})
    ResponseEntity<ErrorResponse> handleUpstreamFailure(Exception ex) {
        if (ex instanceof WeatherApiException && ex.getMessage().contains("not configured")) {
            return response(HttpStatus.SERVICE_UNAVAILABLE,
                    "Weather service is not configured. Set OPENWEATHER_API_KEY and restart the application");
        }
        return response(HttpStatus.BAD_GATEWAY, "Unable to retrieve weather data right now");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected server error occurred");
    }

    private ResponseEntity<ErrorResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message, Instant.now()));
    }
}
