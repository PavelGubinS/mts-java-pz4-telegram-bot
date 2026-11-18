package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Сервис для получения текущей погоды через открытый API Open-Meteo.
 */
@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Возвращает отформатированную сводку погоды для указанного города.
     *
     * @param city название города (например, Екатеринбург или Москва)
     * @return строка с температурой и скоростью ветра либо описание ошибки
     */
    public String getWeather(String city) {
        if (city == null || city.isBlank()) {
            return "Пожалуйста, укажите название города. Пример: /weather Екатеринбург";
        }

        try {
            URI geoUri = UriComponentsBuilder
                    .fromUriString("https://open-meteo.com")
                    .queryParam("name", city.trim())
                    .queryParam("count", 1)
                    .queryParam("language", "ru")
                    .queryParam("format", "json")
                    .encode()
                    .build()
                    .toUri();

            String geoResponse = fetchJson(geoUri);
            JsonNode geoRoot = objectMapper.readTree(geoResponse);
            JsonNode results = geoRoot.path("results");

            if (!results.isArray() || results.isEmpty()) {
                return "Город «" + city + "» не найден. Проверьте правильность написания.";
            }

            JsonNode location = results.get(0);
            String resolvedName = location.path("name").asText(city);
            String country = location.path("country").asText("");
            double latitude = location.path("latitude").asDouble();
            double longitude = location.path("longitude").asDouble();

            URI weatherUri = UriComponentsBuilder
                    .fromUriString("https://open-meteo.com")
                    .queryParam("latitude", latitude)
                    .queryParam("longitude", longitude)
                    .queryParam("current_weather", true)
                    .build()
                    .toUri();

            String weatherResponse = fetchJson(weatherUri);
            JsonNode currentWeather = objectMapper.readTree(weatherResponse).path("current_weather");

            double temperature = currentWeather.path("temperature").asDouble();
            double windSpeed = currentWeather.path("windspeed").asDouble();

            String locationTitle = country.isEmpty() ? resolvedName : resolvedName + " (" + country + ")";
            return String.format(
                    "Погода в г. %s:%nТемпература: %+.1f °C%nСкорость ветра: %.1f км/ч",
                    locationTitle, temperature, windSpeed);
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось получить данные о погоде. Внешний сервис временно недоступен.";
        }
    }

    private String fetchJson(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) WeatherBot/1.0");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
