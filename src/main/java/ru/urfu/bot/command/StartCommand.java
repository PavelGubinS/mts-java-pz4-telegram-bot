package ru.urfu.bot.command;

import org.springframework.stereotype.Component;

/**
 * Обработчик команды /start.
 */
@Component
public class StartCommand implements BotCommand {

    @Override
    public boolean supports(String messageText) {
        return "/start".equalsIgnoreCase(messageText.trim());
    }

    @Override
    public String execute(String messageText) {
        return "Добро пожаловать! Я бот-помощник для просмотра погоды и курсов валют.\n\n"
                + "Воспользуйтесь кнопками ниже или отправьте команду:\n"
                + "• /weather <город> — узнать текущую погоду\n"
                + "• /currency — узнать курс валют ЦБ РФ\n"
                + "• /help — подробная справка";
    }
}
