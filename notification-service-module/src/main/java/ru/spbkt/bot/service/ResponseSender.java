package ru.spbkt.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Интерфейс-обертка для отправки сообщений.
 */
public interface ResponseSender {
    void sendMessage(SendMessage message);
    void sendMessage(Long chatId, String text);
}
