package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Сервис для поиска книг через открытый JSON API Open Library.
 */
@Service
public class BookService {

    private static final String SEARCH_ENDPOINT = "https://openlibrary.org";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 10809)))
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public String searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return "Укажите название книги или автора. Пример: /book Clean Code";
        }

        try {
            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create(SEARCH_ENDPOINT + "?q=" + encoded + "&limit=3");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("User-Agent", "Java-Telegram-Bot-UrFU")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode docs = objectMapper.readTree(response.body()).path("docs");

            if (!docs.isArray() || docs.isEmpty()) {
                return "По запросу «" + query.trim() + "» книги не найдены.";
            }

            StringBuilder sb = new StringBuilder("Найденные книги по запросу «" + query.trim() + "»:\n\n");
            for (int i = 0; i < docs.size(); i++) {
                JsonNode book = docs.get(i);
                String title = book.path("title").asText("Без названия");
                int year = book.path("first_publish_year").asInt(0);
                JsonNode authors = book.path("author_name");
                String author = (authors.isArray() && !authors.isEmpty()) ? authors.get(0).asText()
                        : "Автор неизвестен";

                String yearText = year > 0 ? " (" + year + " г.)" : "";
                sb.append(String.format("%d. «%s» — %s%s%n", i + 1, title, author, yearText));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось выполнить поиск книг. Внешний сервис временно недоступен.";
        }
    }
}
