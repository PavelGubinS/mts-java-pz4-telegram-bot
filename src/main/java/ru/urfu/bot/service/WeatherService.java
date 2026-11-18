package ru.urfu.bot.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.Map;

/**
 * Сервис для получения текущей погоды через открытый XML-сервис Яндекс.Погоды.
 */
@Service
public class WeatherService {

    private static final Map<String, Integer> CITY_REGIONS = Map.ofEntries(
            Map.entry("екатеринбург", 54),
            Map.entry("москва", 213),
            Map.entry("санкт-петербург", 2),
            Map.entry("питер", 2),
            Map.entry("новосибирск", 65),
            Map.entry("казань", 43),
            Map.entry("нижний новгород", 47),
            Map.entry("челябинск", 56),
            Map.entry("самара", 51),
            Map.entry("омск", 66),
            Map.entry("ростов-на-дону", 39),
            Map.entry("уфа", 172),
            Map.entry("красноярск", 62),
            Map.entry("пермь", 50),
            Map.entry("воронеж", 193),
            Map.entry("волгоград", 38),
            Map.entry("тюмень", 55));

    /**
     * Возвращает отформатированную сводку погоды для указанного города.
     *
     * @param city название города (например, Екатеринбург или Москва)
     * @return строка с температурой, состоянием неба и скоростью ветра либо
     *         описание ошибки
     */
    public String getWeather(String city) {
        if (city == null || city.isBlank()) {
            return "Пожалуйста, укажите название города. Пример: /weather Екатеринбург";
        }

        String normalizedCity = city.trim().toLowerCase();
        Integer regionId = CITY_REGIONS.get(normalizedCity);

        if (regionId == null) {
            return "Город «" + city.trim() + "» пока не поддерживается. Доступные города: "
                    + "Екатеринбург, Москва, Санкт-Петербург, Казань, Челябинск, Тюмень, Пермь, Новосибирск и др.";
        }

        try {
            String urlString = "https://yandex.ru" + regionId;
            URLConnection connection = URI.create(urlString).toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                NodeList weatherNodes = document.getElementsByTagName("weather");
                if (weatherNodes.getLength() == 0) {
                    return "Не удалось получить погодную сводку для города «" + city.trim() + "».";
                }

                Element weather = (Element) weatherNodes.item(0);
                Element day = (Element) weather.getElementsByTagName("day").item(0);
                Element dayPart = (Element) day.getElementsByTagName("day_part").item(0);

                String temp = getTagText(dayPart, "temperature");
                if (temp.isEmpty()) {
                    String from = getTagText(dayPart, "temperature_from");
                    String to = getTagText(dayPart, "temperature_to");
                    temp = from + "..." + to;
                }
                String weatherType = getTagText(dayPart, "weather_type");
                String windSpeed = getTagText(dayPart, "wind_speed");
                String humidity = getTagText(dayPart, "humidity");

                return String.format(
                        "Погода в г. %s (Яндекс.Погода):%n" +
                                "Состояние: %s%n" +
                                "Температура: %s °C%n" +
                                "Ветер: %s м/с, влажность: %s%%",
                        capitalize(city.trim()), weatherType, temp, windSpeed, humidity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось получить данные о погоде для города «" + city.trim()
                    + "». Проверьте подключение к сети.";
        }
    }

    private String getTagText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() > 0) {
            return list.item(0).getTextContent().trim();
        }
        return "";
    }

    private String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
