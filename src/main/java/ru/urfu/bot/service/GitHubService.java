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
 * Сервис для поиска популярных репозиториев через открытый GitHub REST API.
 */
@Service
public class GitHubService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 10809)))
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public String searchRepositories(String query) {
        if (query == null || query.isBlank()) {
            return "Укажите тему для поиска. Пример: /repo spring boot";
        }

        try {
            String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
            String url = "https://github.com" + encoded + "&sort=stars&per_page=3";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java-Telegram-Bot-UrFU")
                    .header("Accept", "application/vnd.github.v3+json")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode items = objectMapper.readTree(response.body()).path("items");

            if (!items.isArray() || items.isEmpty()) {
                return "По запросу «" + query.trim() + "» репозитории не найдены.";
            }

            StringBuilder sb = new StringBuilder("Топ-3 репозитория GitHub по запросу «" + query.trim() + "»:\n\n");
            for (int i = 0; i < items.size(); i++) {
                JsonNode repo = items.get(i);
                String fullName = repo.path("full_name").asText();
                int stars = repo.path("stargazers_count").asInt();
                String description = repo.path("description").asText("Без описания");
                if (description.length() > 90) {
                    description = description.substring(0, 87) + "...";
                }
                sb.append(String.format("%d. %s (★ %d)%n   %s%n", i + 1, fullName, stars, description));
            }
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Не удалось получить данные из GitHub API. Попробуйте повторить запрос позже.";
        }
    }
}
