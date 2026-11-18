package ru.urfu.bot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
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
 * Основной класс Telegram-бота, маршрутизирующий входящие сообщения по
 * командам.
 */
public class HelperTelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final List<BotCommand> commands;

    public HelperTelegramBot(
            DefaultBotOptions options,
            String botToken,
            String botUsername,
            List<BotCommand> commands) {
        super(options, botToken);
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
            sendNotification(chatId, "Я обрабатываю только текстовые сообщения. Нажмите /help для справки.");
            return;
        }

        String responseText = resolveResponse(message.getText());
        sendNotification(chatId, responseText);
    }

    private String resolveResponse(String messageText) {
        for (BotCommand command : commands) {
            if (command.supports(messageText)) {
                return command.execute(messageText);
            }
        }
        return "Неизвестная команда. Воспользуйтесь кнопками ниже или введите /help.";
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
        firstRow.add("Книги по Java");
        firstRow.add("Книги Clean Code");

        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("Топ проектов GitHub");
        secondRow.add("Справка");

        keyboard.add(firstRow);
        keyboard.add(secondRow);

        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);
        return markup;
    }
}
