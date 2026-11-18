package ru.urfu.bot.command;

import org.springframework.stereotype.Component;

/**
 * Обработчик стартовой команды /start.
 */
@Component
public class StartCommand implements BotCommand {

    @Override
    public boolean supports(String messageText) {
        return "/start".equalsIgnoreCase(messageText.trim());
    }

    @Override
    public String execute(String messageText) {
        return "Добро пожаловать! Я бот-помощник для поиска книг и IT-проектов.\n\n"
                + "Используйте кнопки меню ниже или команды:\n"
                + "• /book <название> — найти книгу (Open Library API)\n"
                + "• /repo <запрос> — найти популярные репозитории (GitHub API)\n"
                + "• /help — подробная справка";
    }
}
