package ru.spbkt.bot.service;

import ru.spbkt.bot.model.UserContext;

public interface UserContextService {
    /**
     * Получает контекст пользователя по ID чата.
     * Если контекста нет — создает новый.
     */
    UserContext getUserContext(Long chatId, Long telegramId);

    /**
     * Сохраняет/обновляет контекст (для In-Memory это может быть формальностью,
     * но полезно, если потом переедем на Redis).
     */
    void saveUserContext(UserContext context);
}
