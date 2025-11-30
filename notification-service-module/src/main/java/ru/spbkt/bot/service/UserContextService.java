package ru.spbkt.bot.service;

import ru.spbkt.bot.model.UserContext;

public interface UserContextService {
    UserContext getContext(Long chatId);
    void saveContext(UserContext context);
    void clearContext(Long chatId);
}
