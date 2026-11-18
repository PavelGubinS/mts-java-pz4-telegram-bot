package ru.urfu.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.urfu.bot.command.BotCommand;

import java.util.List;

@Configuration
public class BotConfig {

    @Bean
    public DefaultBotOptions defaultBotOptions() {
        DefaultBotOptions options = new DefaultBotOptions();
        options.setProxyHost("127.0.0.1");
        options.setProxyPort(10809);
        options.setProxyType(DefaultBotOptions.ProxyType.HTTP);
        return options;
    }

    @Bean
    public HelperTelegramBot helperTelegramBot(
            DefaultBotOptions options,
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            List<BotCommand> commands) {
        return new HelperTelegramBot(options, botToken, botUsername, commands);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(HelperTelegramBot bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }
}
