package ru.spbkt.bot.listener;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.config.BotProperties;
import ru.spbkt.bot.service.UpdateDispatcher;
import ru.spbkt.bot.service.impl.ResponseSenderImpl;

@Slf4j
@Component
public class TelegramBotListener extends TelegramLongPollingBot {

    private final BotProperties properties;
    private final UpdateDispatcher dispatcher;
    private final ResponseSenderImpl responseSender;

    public TelegramBotListener(BotProperties properties, UpdateDispatcher dispatcher, ResponseSenderImpl responseSender) {
        super(properties.getToken());
        this.properties = properties;
        this.dispatcher = dispatcher;
        this.responseSender = responseSender;
    }

    @PostConstruct
    public void initialize() {
        // Устанавливаем ссылку на объект бота в сервис отправки
        responseSender.setTelegramBot(this);
        log.info("TelegramBotListener initialized: @{}", properties.getUsername());
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        dispatcher.dispatch(update);
    }
}
