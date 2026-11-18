package ru.urfu.bot.command;

import org.springframework.stereotype.Component;

/**
 * Обработчик команды /help и кнопки «Справка».
 */
@Component
public class HelpCommand implements BotCommand {

    @Override
    public boolean supports(String messageText) {
        String normalized = messageText.trim();
        return "/help".equalsIgnoreCase(normalized) || "Справка".equalsIgnoreCase(normalized);
    }

    @Override
    public String execute(String messageText) {
        return "Доступные команды бота:\n\n"
                + "1. /book <название> — поиск книг по названию или автору (например: /book Гарри Поттер).\n"
                + "2. /repo <тема> — поиск популярных проектов на GitHub (например: /repo telegram bot).\n"
                + "3. Кнопки быстрого поиска внизу экрана.";
    }
}
