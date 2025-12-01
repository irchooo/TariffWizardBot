package ru.spbkt.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;

public interface InputHandler {
    /**
     * Метод обработки сообщения.
     * @param update Входящее обновление от Telegram.
     * @param context Текущий контекст пользователя.
     */
    void handle(Update update, UserContext context);

    /**
     * Возвращает состояние, которое этот хендлер обрабатывает.
     * Это поможет Dispatcher-у выбирать нужный хендлер.
     */
    BotState getHandlerName();
}
