package ru.urfu.bot.command;

import org.springframework.stereotype.Component;
import ru.urfu.bot.service.CurrencyService;

/**
 * Обработчик команды /currency и кнопки «Курс валют».
 */
@Component
public class CurrencyCommand implements BotCommand {

    private final CurrencyService currencyService;

    public CurrencyCommand(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public boolean supports(String messageText) {
        String normalized = messageText.trim();
        return "/currency".equalsIgnoreCase(normalized) || "Курс валют".equalsIgnoreCase(normalized);
    }

    @Override
    public String execute(String messageText) {
        return currencyService.getCurrencyRates();
    }
}
