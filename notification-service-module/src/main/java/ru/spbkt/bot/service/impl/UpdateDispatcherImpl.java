package ru.spbkt.bot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.handler.InputHandler;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.service.UpdateDispatcher;
import ru.spbkt.bot.util.KeyboardFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UpdateDispatcherImpl implements UpdateDispatcher {

    private final Map<BotState, InputHandler> handlers;
    private final ClientServiceClient clientServiceClient;
    private final ResponseSender responseSender;

    // Spring автоматически соберет все бины, реализующие InputHandler, в список
    public UpdateDispatcherImpl(List<InputHandler> handlerList,
                                ClientServiceClient clientServiceClient,
                                ResponseSender responseSender) {
        this.clientServiceClient = clientServiceClient;
        this.responseSender = responseSender;

        Map<BotState, InputHandler> handlerMap = new HashMap<>();

        // 2. Находим RegistrationHandler и маппим его на оба состояния
        InputHandler registrationHandler = handlerList.stream()
                .filter(h -> h.getClass().getSimpleName().equals("RegistrationHandler"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("RegistrationHandler not found in context."));

        handlerMap.put(BotState.WAITING_NAME, registrationHandler);
        handlerMap.put(BotState.WAITING_PHONE, registrationHandler);

        // 3. Маппим остальные хендлеры (пока их нет, но для MenuNavigationHandler будет BotState.MAIN_MENU)
        handlerList.stream()
                .filter(h -> !h.getClass().getSimpleName().equals("RegistrationHandler"))
                .forEach(h -> handlerMap.put(h.getHandlerName(), h));

        this.handlers = handlerMap;
    }

    @Override
    public void dispatch(Update update, UserContext context) {
        if (!update.hasMessage()) return;

        String text = update.getMessage().hasText() ? update.getMessage().getText() : "";

        // 1. Обработка команды /start (всегда приоритетна)
        if ("/start".equals(text)) {
            handleStart(context);
            return;
        }

        // 2. Поиск обработчика для текущего состояния
        InputHandler handler = handlers.get(context.getState());

        if (handler != null) {
            handler.handle(update, context);
        } else {
            // Если хендлер не найден (например, мы еще не написали MenuHandler)
            log.warn("Нет обработчика для состояния: {}", context.getState());
            responseSender.sendMessage(context.getChatId(), "Неизвестная команда или состояние. Введите /start");
        }
    }

    /**
     * Логика при нажатии /start:
     * - Если клиент есть в базе -> Главное меню.
     * - Если клиента нет -> Начинаем регистрацию.
     */
    private void handleStart(UserContext context) {
        // Проверяем через Client-Service (Сценарий 1.1)
        boolean exists = clientServiceClient.clientExists(context.getTelegramId());

        if (exists) {
            context.setState(BotState.MAIN_MENU);
            responseSender.sendMessage(context.getChatId(),
                    "С возвращением! Выберите действие:",
                    KeyboardFactory.getMainMenuKeyboard());
        } else {
            context.setState(BotState.WAITING_NAME);
            responseSender.sendMessage(context.getChatId(),
                    "Добро пожаловать в Tariff Wizard Bot!\nДавайте зарегистрируемся. Введите ваше Имя и Фамилию:");
        }
    }
}
