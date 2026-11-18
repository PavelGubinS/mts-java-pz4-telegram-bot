package ru.urfu.bot.command;

/**
 * Базовый интерфейс команды бота (паттерн Command).
 */
public interface BotCommand {

    boolean supports(String messageText);

    String execute(String messageText);
}
