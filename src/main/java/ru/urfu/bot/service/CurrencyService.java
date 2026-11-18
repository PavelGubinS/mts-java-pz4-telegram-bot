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
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для получения актуальных курсов валют с официального портала Центрального банка РФ.
 */
@Service
public class CurrencyService {

    private static final String CBR_XML_URL = "https://cbr.ru";

    /**
     * Возвращает актуальные курсы USD, EUR и CNY к рублю.
     *
     * @return отформатированный список курсов валют либо сообщение об ошибке
     */
    public String getCurrencyRates() {
        try {
            URLConnection connection = URI.create(CBR_XML_URL).toURL().openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            Map<String, Double> rates = new HashMap<>();

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(inputStream);

                NodeList valutes = document.getElementsByTagName("Valute");
                for (int i = 0; i < valutes.getLength(); i++) {
                    Element valute = (Element) valutes.item(i);
                    String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                    if ("USD".equals(charCode) || "EUR".equals(charCode) || "CNY".equals(charCode)) {
                        String valueStr = valute.getElementsByTagName("Value").item(0).getTextContent().replace(',', '.');
                        String nominalStr = valute.getElementsByTagName("Nominal").item(0).getTextContent();
                        double rate = Double.parseDouble(valueStr) / Double.parseDouble(nominalStr);
                        rates.put(charCode, rate);
                    }
                }
            }

            if (rates.isEmpty()) {
                return "Не удалось распознать данные о курсах валют.";
            }

            return String.format(
                    "Актуальный курс валют ЦБ РФ:%n" +
                    "Доллар США (USD): %.2f руб.%n" +
                    "Евро (EUR): %.2f руб.%n" +
                    "Китайский юань (CNY): %.2f руб.",
                    rates.getOrDefault("USD", 0.0),
                    rates.getOrDefault("EUR", 0.0),
                    rates.getOrDefault("CNY", 0.0)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось загрузить курсы валют с сайта ЦБ РФ. Попробуйте повторить запрос позже.";
        }
    }
}
