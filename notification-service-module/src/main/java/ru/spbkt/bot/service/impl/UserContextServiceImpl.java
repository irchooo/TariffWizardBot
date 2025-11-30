package ru.spbkt.bot.service.impl;

import org.springframework.stereotype.Service;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.UserContextService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class UserContextServiceImpl implements UserContextService {

    // Хранение контекстов в памяти (для MVP)
    private final ConcurrentMap<Long, UserContext> contextMap = new ConcurrentHashMap<>();

    @Override
    public UserContext getContext(Long chatId) {
        // Создаем новый контекст, если не найден
        return contextMap.computeIfAbsent(chatId, UserContext::new);
    }

    @Override
    public void saveContext(UserContext context) {
        contextMap.put(context.getChatId(), context);
    }

    @Override
    public void clearContext(Long chatId) {
        contextMap.remove(chatId);
    }
}
