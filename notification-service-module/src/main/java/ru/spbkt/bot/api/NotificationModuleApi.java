package ru.spbkt.bot.api;

public interface NotificationModuleApi {

    /**
     * Отправить текстовое сообщение пользователю в Telegram.
     */
    void sendNotification(Long telegramId, String text);
}