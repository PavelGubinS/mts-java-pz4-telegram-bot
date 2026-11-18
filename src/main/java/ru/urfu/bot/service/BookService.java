package ru.urfu.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Сервис для получения списка книг по HTTP/JSON из REST API.
 */
@Service
public class BookService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public String searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return "Укажите название книги или автора. Пример: /book Clean Code";
        }

        try {
            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create("http://localhost:8080/api/books?q=" + encoded);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode items = objectMapper.readTree(response.body()).path("items");

            if (!items.isArray() || items.isEmpty()) {
                return "По запросу «" + query.trim()
                        + "» книги не найдены. Попробуйте: Java, Clean Code, Spring или алгоритмы.";
            }

            StringBuilder sb = new StringBuilder("Найденные книги по запросу «" + query.trim() + "»:\n\n");
            for (int i = 0; i < items.size(); i++) {
                JsonNode book = items.get(i);
                String title = book.path("title").asText();
                String author = book.path("author").asText();
                int year = book.path("year").asInt();
                sb.append(String.format("%d. «%s» — %s (%d г.)%n", i + 1, title, author, year));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось выполнить поиск книг.";
        }
    }
}
