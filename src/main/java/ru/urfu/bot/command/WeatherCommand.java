package ru.urfu.bot.command;

import org.springframework.stereotype.Component;
import ru.urfu.bot.service.WeatherService;

/**
 * Обработчик команды /weather и кнопок быстрого просмотра погоды.
 */
@Component
public class WeatherCommand implements BotCommand {

    private static final String BTN_EKB = "Погода в Екатеринбурге";
    private static final String BTN_MSK = "Погода в Москве";

    private final WeatherService weatherService;

    public WeatherCommand(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public boolean supports(String messageText) {
        String normalized = messageText.trim();
        return normalized.toLowerCase().startsWith("/weather")
                || BTN_EKB.equalsIgnoreCase(normalized)
                || BTN_MSK.equalsIgnoreCase(normalized);
    }

    @Override
    public String execute(String messageText) {
        String normalized = messageText.trim();
        if (BTN_EKB.equalsIgnoreCase(normalized)) {
            return weatherService.getWeather("Екатеринбург");
        }
        if (BTN_MSK.equalsIgnoreCase(normalized)) {
            return weatherService.getWeather("Москва");
        }
        String city = normalized.substring("/weather".length()).trim();
        return weatherService.getWeather(city);
    }
}
