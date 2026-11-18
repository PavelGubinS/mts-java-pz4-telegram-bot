package ru.urfu.bot.command;

/**
 * Базовый интерфейс команды бота (паттерн Command).
 */
public interface BotCommand {

    /**
     * Проверяет, подходит ли данная команда для обработки текста пользователя.
     *
     * @param messageText текст сообщения от пользователя
     * @return true, если команда может обработать этот текст
     */
    boolean supports(String messageText);

    /**
     * Выполняет логику команды и формирует текстовый ответ.
     *
     * @param messageText исходный текст сообщения от пользователя
     * @return готовый ответ для отправки в чат
     */
    String execute(String messageText);
}
