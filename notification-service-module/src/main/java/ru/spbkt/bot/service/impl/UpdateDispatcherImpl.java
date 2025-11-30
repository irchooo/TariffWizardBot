package ru.spbkt.bot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.service.UpdateDispatcher;
import ru.spbkt.bot.service.UserContextService;
import ru.spbkt.bot.handler.InputHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UpdateDispatcherImpl implements UpdateDispatcher {

    private final UserContextService contextService;
    private final ResponseSender responseSender;

    private final Map<BotState, InputHandler> handlers;

    // Сбор всех хендлеров Spring-ом
    public UpdateDispatcherImpl(UserContextService contextService, ResponseSender responseSender,
                                List<InputHandler> handlerList) {
        this.contextService = contextService;
        this.responseSender = responseSender;
        this.handlers = new HashMap<>();
        for (InputHandler handler : handlerList) {
            for (BotState state : handler.getSupportedStates()) {
                handlers.put(state, handler);
            }
        }
        log.info("Registered {} handlers: {}", handlers.size(), handlers.keySet());
    }

    @Override
    public void dispatch(Update update) {
        Long chatId = getChatId(update);
        if (chatId == null) return;

        UserContext context = contextService.getContext(chatId);

        // ВАЖНО: Определяем текущий обработчик по состоянию
        InputHandler currentHandler = handlers.get(context.getCurrentState());

        if (currentHandler == null) {
            log.error("No handler found for state: {}. Resetting to START.", context.getCurrentState());
            context.setCurrentState(BotState.START);
            contextService.saveContext(context);
            currentHandler = handlers.get(BotState.START);
        }

        try {
            currentHandler.handle(update, context);
        } catch (Exception e) {
            log.error("Error during handling update in state {}: {}", context.getCurrentState(), e.getMessage(), e);
            responseSender.sendMessage(chatId, "Произошла внутренняя ошибка. Пожалуйста, начните сначала с команды /start.");
            context.setCurrentState(BotState.START);
        }

        contextService.saveContext(context);
    }

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }
}
