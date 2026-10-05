const statusEl = document.querySelector("#status");
const dashboard = document.querySelector("#dashboard");
const iconUrl = icon => `https://openweathermap.org/img/wn/${icon}@2x.png`;
const themeToggle = document.querySelector("#theme-toggle");
const themeIcon = document.querySelector("#theme-icon");
const themeLabel = document.querySelector("#theme-label");
let automaticTheme = true;

function applyTheme(theme) {
    document.documentElement.dataset.theme = theme;
    const night = theme === "night";
    themeIcon.textContent = night ? "☀" : "☾";
    themeLabel.textContent = night ? "Day mode" : "Night mode";
    themeToggle.setAttribute("aria-label", night ? "Switch to day mode" : "Switch to night mode");
}

function applyAutomaticTheme(sunrise, sunset) {
    if (!automaticTheme) return;
    const now = Date.now() / 1000;
    applyTheme(now < sunrise || now >= sunset ? "night" : "day");
}

themeToggle.addEventListener("click", () => {
    automaticTheme = false;
    applyTheme(document.documentElement.dataset.theme === "night" ? "day" : "night");
});

function setStatus(message, error = false) {
    statusEl.textContent = message;
    statusEl.classList.toggle("error", error);
}

function displayWeather(weather) {
    const current = weather.current;
    document.querySelector("#city").textContent = weather.city;
    document.querySelector("#country").textContent = weather.country;
    document.querySelector("#current-icon").src = iconUrl(current.icon);
    document.querySelector("#current-icon").alt = current.description;
    document.querySelector("#temperature").textContent = Math.round(current.temperature);
    document.querySelector("#condition").textContent = current.condition;
    document.querySelector("#description").textContent = current.description;
    document.querySelector("#feels-like").textContent = `${Math.round(current.feelsLike)}°C`;
    document.querySelector("#humidity").textContent = `${current.humidity}%`;
    document.querySelector("#wind").textContent = `${current.windSpeed.toFixed(1)} m/s`;
    document.querySelector("#pressure").textContent = `${current.pressure} hPa`;
    document.querySelector("#sunrise").textContent = formatTime(current.sunrise);
    document.querySelector("#sunset").textContent = formatTime(current.sunset);
    applyAutomaticTheme(current.sunrise, current.sunset);
    document.querySelector("#forecast").innerHTML = weather.forecast.map(day => `
        <article class="forecast-day">
            <time>${day.date}</time>
            <img src="${iconUrl(day.icon)}" alt="${day.description}">
            <strong>${Math.round(day.temperature)}°</strong>
            <small>${Math.round(day.minimumTemperature)}° / ${Math.round(day.maximumTemperature)}°</small>
        </article>`).join("");
    dashboard.hidden = false;
    setStatus(`Updated just now · ${weather.city}`);
}

function formatTime(seconds) {
    return new Date(seconds * 1000).toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
}

async function requestWeather(url) {
    dashboard.hidden = true;
    setStatus("Fetching weather data...");
    try {
        const response = await fetch(url);
        const data = await response.json();
        if (!response.ok) {
            const error = new Error(data.message || "Unable to retrieve weather data");
            error.status = response.status;
            throw error;
        }
        displayWeather(data);
    } catch (error) {
        setStatus(error.status === 404 ? "City not found."
            : error.status === 503 ? "Weather service is not configured. Set OPENWEATHER_API_KEY and restart the server."
            : "Unable to retrieve weather data. Please try again.", true);
    }
}

document.querySelector("#search-form").addEventListener("submit", event => {
    event.preventDefault();
    const city = document.querySelector("#city-input").value.trim();
    if (!city) {
        setStatus("Please enter a city to search.", true);
        return;
    }
    requestWeather(`/api/weather/city?name=${encodeURIComponent(city)}`);
});

function detectLocation() {
    if (!navigator.geolocation) {
        setStatus("Unable to detect your location. Please search for a city manually.", true);
        return;
    }
    navigator.geolocation.getCurrentPosition(
        position => requestWeather(`/api/weather/current?lat=${position.coords.latitude}&lon=${position.coords.longitude}`),
        error => setStatus(error.code === 1
            ? "Location access was denied. Search for a city instead."
            : "Unable to detect your location. Please search for a city manually.", true),
        { enableHighAccuracy: false, timeout: 10000 }
    );
}

detectLocation();
