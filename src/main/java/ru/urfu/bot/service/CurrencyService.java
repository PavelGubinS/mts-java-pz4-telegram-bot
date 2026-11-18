package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис для получения актуальных курсов валют от Центрального банка РФ.
 */
@Service
public class CurrencyService {

    private static final String CBR_DAILY_URL = "https://cbr-xml-daily.ru";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Возвращает актуальные курсы USD, EUR и CNY к рублю.
     *
     * @return отформатированный список курсов валют либо сообщение об ошибке
     */
    public String getCurrencyRates() {
        try {
            String response = restTemplate.getForObject(CBR_DAILY_URL, String.class);
            JsonNode valute = objectMapper.readTree(response).path("Valute");

            double usd = valute.path("USD").path("Value").asDouble();
            double eur = valute.path("EUR").path("Value").asDouble();
            double cny = valute.path("CNY").path("Value").asDouble();

            return String.format(
                    "Актуальный курс валют ЦБ РФ:%n" +
                            "Доллар США (USD): %.2f руб.%n" +
                            "Евро (EUR): %.2f руб.%n" +
                            "Китайский юань (CNY): %.2f руб.",
                    usd, eur, cny);
        } catch (Exception e) {
            return "Не удалось загрузить курсы валют. Попробуйте повторить запрос позже.";
        }
    }
}
