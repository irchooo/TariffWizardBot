package ru.spbkt.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.spbkt.bot.listener.TelegramBotListener;

@Configuration
public class BotConfig {

    /**
     * Этот метод регистрирует нашего бота (TelegramBotListener) в API Telegram.
     * Сам класс TelegramBotListener должен быть помечен аннотацией @Component.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotListener bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }
}
