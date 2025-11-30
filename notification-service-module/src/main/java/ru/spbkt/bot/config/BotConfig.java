package ru.spbkt.bot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.spbkt.bot.listener.TelegramBotListener;

@Configuration
@RequiredArgsConstructor
public class BotConfig {

    private final TelegramBotListener telegramBotListener;

    /**
     * Регистрирует слушателя (listener) в Telegram API.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(telegramBotListener);
        return api;
    }

    /**
     * RestTemplate для коммуникации с другими сервисами.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
