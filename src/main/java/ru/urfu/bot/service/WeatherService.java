package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Сервис для получения текущей погоды через открытый JSON API wttr.in.
 */
@Service
public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Возвращает отформатированную сводку погоды для указанного города.
     *
     * @param city название города (например, Екатеринбург или Москва)
     * @return строка с температурой, ощущением и скоростью ветра либо описание
     *         ошибки
     */
    public String getWeather(String city) {
        if (city == null || city.isBlank()) {
            return "Пожалуйста, укажите название города. Пример: /weather Екатеринбург";
        }

        try {
            String encodedCity = URLEncoder.encode(city.trim(), StandardCharsets.UTF_8);
            String urlString = "https://wttr.in" + encodedCity + "?format=j1&lang=ru";

            URLConnection connection = URI.create(urlString).toURL().openConnection();
            connection.setRequestProperty("User-Agent", "curl/8.0");
            connection.setConnectTimeout(7000);
            connection.setReadTimeout(7000);

            try (InputStream inputStream = connection.getInputStream()) {
                JsonNode root = objectMapper.readTree(inputStream);
                JsonNode currentList = root.path("current_condition");

                if (!currentList.isArray() || currentList.isEmpty()) {
                    return "Город «" + city + "» не найден. Проверьте правильность написания.";
                }

                JsonNode current = currentList.get(0);
                String tempC = current.path("temp_C").asText();
                String feelsLikeC = current.path("FeelsLikeC").asText();
                String windKmph = current.path("windspeedKmph").asText();
                String humidity = current.path("humidity").asText();

                String description = "";
                JsonNode langRu = current.path("lang_ru");
                if (langRu.isArray() && !langRu.isEmpty()) {
                    description = langRu.get(0).path("value").asText("");
                }

                return String.format(
                        "Погода в г. %s:%n" +
                                "Состояние: %s%n" +
                                "Температура: %s °C (ощущается как %s °C)%n" +
                                "Ветер: %s км/ч, влажность: %s%%",
                        city.trim(),
                        description.isEmpty() ? "нет описания" : description,
                        tempC, feelsLikeC, windKmph, humidity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось получить данные о погоде для города «" + city + "». Попробуйте позже.";
        }
    }
}
