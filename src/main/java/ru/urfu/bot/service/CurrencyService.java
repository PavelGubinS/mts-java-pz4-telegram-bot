
package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) CurrencyBot/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    CBR_DAILY_URL, HttpMethod.GET, entity, String.class);
            JsonNode valute = objectMapper.readTree(response.getBody()).path("Valute");

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
            e.printStackTrace();
            return "Не удалось загрузить курсы валют. Попробуйте повторить запрос позже.";
        }
    }
}
