# Weather Forecast Application

## Overview

Weather Forecast Application is a small, deployable Spring Boot and vanilla JavaScript project. It detects a visitor's location with the browser Geolocation API, retrieves weather through a protected backend integration, and provides city search as a fallback.

## Features

- Current weather based on latitude and longitude
- City search using OpenWeatherMap geocoding
- Five-day forecast cards
- Responsive mobile-friendly dashboard
- Loading, validation, permission, not-found, and upstream error states
- API key kept on the server through an environment variable

## Technologies Used

Java 17, Spring Boot 3, Maven, Spring Web `RestClient`, Bean Validation, JUnit 5, HTML, CSS, and vanilla JavaScript.

## Project Architecture

- `controller`: exposes the three application endpoints.
- `service`: validates input, calls OpenWeatherMap, and maps provider JSON to small DTOs.
- `dto`: typed objects returned to the browser.
- `exception`: consistent JSON error responses without stack traces.
- `config`: creates the reusable external API client.
- `src/main/resources/static`: frontend served by Spring Boot.

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/weather/current?lat=28.6&lon=77.2` | Current weather and forecast by coordinates |
| GET | `/api/weather/forecast?lat=28.6&lon=77.2` | Same weather payload for forecast clients |
| GET | `/api/weather/city?name=Delhi` | Geocode a city and load its weather |

## Local Setup

1. Install Java 17 or later and Maven.
2. Create an OpenWeatherMap account and generate an API key.
3. Set the environment variable before starting the application:

   **PowerShell:** `$env:OPENWEATHER_API_KEY="your-key"`

   If you start the app from the VS Code Run/Debug button instead of the terminal, add
   `OPENWEATHER_API_KEY` to the Java launch configuration or restart VS Code after setting
   the variable. Spring Boot only receives environment variables from the process that starts it.

4. Run `mvn spring-boot:run`.
5. Open `http://localhost:8080`.

The API key is intentionally not stored in `application.properties`, JavaScript, or source control. `.env.example` documents the required name.

## Running Tests

```text
mvn test
```

## Deployment to Railway

1. Push this folder to a GitHub repository.
2. Create a new Railway project and deploy from the repository.
3. Add `OPENWEATHER_API_KEY` under Railway service variables.
4. Railway detects the Maven project and runs the Spring Boot application. The app reads Railway's `PORT` variable, with `8080` as the local default.
5. Open the generated HTTPS domain and test both location permission and city search.

Geolocation requires a secure context, so use the Railway HTTPS URL rather than an HTTP URL. Never paste the API key into the repository.

## Screenshots

Run the application locally or deploy it, then capture screenshots for the project report.

## Future Enhancements

Authentication, favorite cities, weather history, a database, alerts, dark mode, air quality, charts, and multiple languages would be natural future improvements.

## Author

College minor project — add your name, course, and institution here.
