package ru.spbkt.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.model.UserContext;

public interface UpdateDispatcher {
    void dispatch(Update update, UserContext context);
}
