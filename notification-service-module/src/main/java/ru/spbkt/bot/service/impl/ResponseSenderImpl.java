package ru.spbkt.bot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.spbkt.bot.service.ResponseSender;

@Slf4j
@Service
public class ResponseSenderImpl implements ResponseSender {

    // Ссылка на сам объект бота (будет установлена из Listener)
    private AbsSender telegramBot;

    public void setTelegramBot(AbsSender telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void sendMessage(SendMessage message) {
        if (telegramBot == null) {
            log.error("TelegramBot is not initialized.");
            return;
        }
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chatId={}: {}", message.getChatId(), e.getMessage());
        }
    }

    @Override
    public void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("HTML")
                .build();
        sendMessage(message);
    }
}
