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
                + "1. /weather <город> — текущая температура и скорость ветра в любом городе (например: /weather Москва).\n"
                + "2. Погода в Екатеринбурге — быстрый прогноз для Екатеринбурга.\n"
                + "3. /currency или кнопка «Курс валют» — официальный курс USD, EUR и CNY от ЦБ РФ.\n"
                + "4. /help или кнопка «Справка» — показать это сообщение.";
    }
}
