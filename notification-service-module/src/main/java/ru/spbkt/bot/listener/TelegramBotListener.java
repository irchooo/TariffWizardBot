package ru.spbkt.bot.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.config.BotProperties;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.UpdateDispatcher;
import ru.spbkt.bot.service.UserContextService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBotListener extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final UpdateDispatcher updateDispatcher;
    private final UserContextService userContextService;

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            // Реагируем только на личные сообщения (Message)
            // (CallbackQuery для Inline кнопок обработаем позже, если добавим их)
            if (update.hasMessage()) {
                Long chatId = update.getMessage().getChatId();
                Long telegramId = update.getMessage().getFrom().getId();

                // 1. Получаем/Создаем контекст пользователя
                UserContext context = userContextService.getUserContext(chatId, telegramId);

                // 2. Передаем в диспетчер
                updateDispatcher.dispatch(update, context);

                // 3. Сохраняем обновленное состояние
                userContextService.saveUserContext(context);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке update: ", e);
        }
    }
}
