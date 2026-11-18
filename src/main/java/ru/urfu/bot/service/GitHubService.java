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
 * Сервис для получения списка IT-репозиториев по HTTP/JSON из REST API.
 */
@Service
public class GitHubService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public String searchRepositories(String query) {
        if (query == null || query.isBlank()) {
            return "Укажите тему для поиска. Пример: /repo java";
        }

        try {
            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            URI uri = URI.create("http://localhost:8080/api/repos?q=" + encoded);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode items = objectMapper.readTree(response.body()).path("items");

            if (!items.isArray() || items.isEmpty()) {
                return "По запросу «" + query.trim() + "» проекты не найдены. Попробуйте: java, spring или telegram.";
            }

            StringBuilder sb = new StringBuilder("Топ репозиториев по запросу «" + query.trim() + "»:\n\n");
            for (int i = 0; i < items.size(); i++) {
                JsonNode repo = items.get(i);
                String fullName = repo.path("full_name").asText();
                int stars = repo.path("stars").asInt();
                String description = repo.path("description").asText();
                sb.append(String.format("%d. %s (★ %d)%n   %s%n", i + 1, fullName, stars, description));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось получить список проектов.";
        }
    }
}
