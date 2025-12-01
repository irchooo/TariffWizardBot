package ru.spbkt.bot.service.impl;

import org.springframework.stereotype.Service;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.UserContextService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserContextServiceImpl implements UserContextService {

    // Хранилище в памяти: Map<ChatId, Context>
    private final Map<Long, UserContext> contextMap = new ConcurrentHashMap<>();

    @Override
    public UserContext getUserContext(Long chatId, Long telegramId) {
        return contextMap.computeIfAbsent(chatId, k -> new UserContext(telegramId, chatId));
    }

    @Override
    public void saveUserContext(UserContext context) {
        contextMap.put(context.getChatId(), context);
    }
}
