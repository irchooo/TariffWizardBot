package ru.spbkt.bot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;

import java.util.List;

public interface InputHandler {
    void handle(Update update, UserContext context);
    List<BotState> getSupportedStates();
}
