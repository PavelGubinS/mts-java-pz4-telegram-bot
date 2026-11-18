package ru.urfu.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.urfu.bot.command.BotCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Основной класс Telegram-бота, принимающий входящие обновления и
 * маршрутизирующий команды.
 */
@Component
public class WeatherTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final List<BotCommand> commands;

    public WeatherTelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<BotCommand> commands) {
        super(botToken);
        this.botUsername = botUsername;
        this.commands = commands;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();
        long chatId = message.getChatId();

        if (!message.hasText()) {
            sendNotification(chatId, "Я понимаю только текстовые сообщения и команды. Нажмите /help для справки.");
            return;
        }

        String messageText = message.getText();
        String responseText = resolveResponse(messageText);
        sendNotification(chatId, responseText);
    }

    private String resolveResponse(String messageText) {
        for (BotCommand command : commands) {
            if (command.supports(messageText)) {
                return command.execute(messageText);
            }
        }
        return "Неизвестная команда. Используйте кнопки меню или отправьте /help, чтобы увидеть список команд.";
    }

    private void sendNotification(long chatId, String text) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(String.valueOf(chatId));
        responseMessage.setText(text);
        responseMessage.setReplyMarkup(buildMainKeyboard());

        try {
            execute(responseMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private ReplyKeyboardMarkup buildMainKeyboard() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("Погода в Екатеринбурге");
        firstRow.add("Погода в Москве");

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("Курс валют");
        secondRow.add("Справка");

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        return markup;
    }
}
