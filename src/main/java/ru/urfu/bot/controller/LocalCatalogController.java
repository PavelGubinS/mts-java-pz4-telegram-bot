package ru.urfu.bot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Локальный REST API каталог книг и IT-проектов в формате JSON.
 */
@RestController
@RequestMapping("/api")
public class LocalCatalogController {

    private static final List<Map<String, Object>> BOOKS = List.of(
            Map.of("title", "Чистый код (Clean Code)", "author", "Роберт Мартин", "year", 2008),
            Map.of("title", "Философия Java (Thinking in Java)", "author", "Брюс Эккель", "year", 2006),
            Map.of("title", "Effective Java", "author", "Джошуа Блох", "year", 2018),
            Map.of("title", "Spring в действии (Spring in Action)", "author", "Крейг Уоллс", "year", 2022),
            Map.of("title", "Грокаем алгоритмы", "author", "Адитья Бхаргава", "year", 2017));

    private static final List<Map<String, Object>> REPOS = List.of(
            Map.of("full_name", "spring-projects/spring-boot", "stars", 76500, "description",
                    "Фреймворк для быстрой разработки приложений на Java"),
            Map.of("full_name", "rubenlagus/TelegramBots", "stars", 4400, "description",
                    "Java-библиотека для создания Telegram-ботов"),
            Map.of("full_name", "iluwatar/java-design-patterns", "stars", 91200, "description",
                    "Реализация паттернов проектирования на языке Java"),
            Map.of("full_name", "torvalds/linux", "stars", 189000, "description",
                    "Исходный код ядра операционной системы Linux"),
            Map.of("full_name", "microsoft/vscode", "stars", 168000, "description",
                    "Редактор кода Visual Studio Code"));

    @GetMapping("/books")
    public Map<String, Object> findBooks(@RequestParam(defaultValue = "") String q) {
        String query = q.trim().toLowerCase();
        List<Map<String, Object>> matched = BOOKS.stream()
                .filter(b -> query.isEmpty()
                        || b.get("title").toString().toLowerCase().contains(query)
                        || b.get("author").toString().toLowerCase().contains(query))
                .limit(3)
                .toList();
        return Map.of("items", matched);
    }

    @GetMapping("/repos")
    public Map<String, Object> findRepos(@RequestParam(defaultValue = "") String q) {
        String query = q.trim().toLowerCase();
        List<Map<String, Object>> matched = REPOS.stream()
                .filter(r -> query.isEmpty()
                        || r.get("full_name").toString().toLowerCase().contains(query)
                        || r.get("description").toString().toLowerCase().contains(query))
                .limit(3)
                .toList();
        return Map.of("items", matched);
    }
}
