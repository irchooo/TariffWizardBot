package ru.spbkt.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ResponseSender {
    void sendMessage(Long chatId, String text);
    // Перегруженный метод для отправки с клавиатурой
    void sendMessage(Long chatId, String text, Object replyMarkup);
    // Метод для "сырого" объекта SendMessage
    void execute(SendMessage message);
}
