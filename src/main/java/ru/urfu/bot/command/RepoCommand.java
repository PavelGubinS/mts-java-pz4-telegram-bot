package ru.urfu.bot.command;

import org.springframework.stereotype.Component;
import ru.urfu.bot.service.GitHubService;

/**
 * Обработчик команды /repo и кнопки быстрого поиска репозиториев на GitHub.
 */
@Component
public class RepoCommand implements BotCommand {

    private static final String BTN_TOP_GITHUB = "Топ проектов GitHub";

    private final GitHubService gitHubService;

    public RepoCommand(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @Override
    public boolean supports(String messageText) {
        String normalized = messageText.trim();
        return normalized.toLowerCase().startsWith("/repo")
                || BTN_TOP_GITHUB.equalsIgnoreCase(normalized);
    }

    @Override
    public String execute(String messageText) {
        String normalized = messageText.trim();
        if (BTN_TOP_GITHUB.equalsIgnoreCase(normalized)) {
            return gitHubService.searchRepositories("java spring boot");
        }
        String query = normalized.substring("/repo".length()).trim();
        return gitHubService.searchRepositories(query);
    }
}
