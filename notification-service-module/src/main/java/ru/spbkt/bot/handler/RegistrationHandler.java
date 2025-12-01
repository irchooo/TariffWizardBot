package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.service.UserContextService;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;

@Component
@RequiredArgsConstructor
public class RegistrationHandler implements InputHandler {

    private final ClientServiceClient clientServiceClient;
    private final ResponseSender responseSender;
    private final UserContextService userContextService;

    // Этот хендлер будет вызываться явно из Dispatcher для состояний регистрации,
    // поэтому getHandlerName можно вернуть любое из них или null, если маппинг ручной.
    // Но для порядка вернем WAITING_NAME.
    @Override
    public BotState getHandlerName() {
        return BotState.WAITING_NAME;
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (context.getState() == BotState.WAITING_NAME) {
            handleNameInput(update, context);
        } else if (context.getState() == BotState.WAITING_PHONE) {
            handlePhoneInput(update, context);
        }
    }

    private void handleNameInput(Update update, UserContext context) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(context.getChatId(), "Пожалуйста, введите ваше Имя и Фамилию текстом.");
            return;
        }

        String fullName = update.getMessage().getText();
        // Временное хранение имени можно сделать через TariffDraft или добавить поле в Context.
        // Для простоты разделим строку и временно сохраним в draft, так как там есть свободные поля,
        // или (лучше) просто запомним, что следующий шаг - телефон, а имя пока некуда класть в текущую модель UserContext.

        // ДАВАЙ ДОБАВИМ В UserContext поле tempRegistrationName (см. примечание ниже кода).
        // Предположим, мы его добавили.
        context.setTempRegistrationData(fullName);

        context.setState(BotState.WAITING_PHONE);
        userContextService.saveUserContext(context);

        responseSender.sendMessage(context.getChatId(),
                "Приятно познакомиться, " + fullName + "! Теперь отправьте ваш номер телефона для завершения регистрации.",
                KeyboardFactory.getRegistrationKeyboard());
    }

    private void handlePhoneInput(Update update, UserContext context) {
        String phoneNumber;

        if (update.getMessage().hasContact()) {
            phoneNumber = update.getMessage().getContact().getPhoneNumber();
        } else if (update.getMessage().hasText()) {
            // Если пользователь ввел руками
            phoneNumber = update.getMessage().getText();
        } else {
            responseSender.sendMessage(context.getChatId(), "Пожалуйста, нажмите кнопку 'Поделиться контактом' или введите номер.");
            return;
        }

        // Парсинг имени из сохраненного ранее
        String fullName = context.getTempRegistrationData();
        String firstName = fullName.split(" ")[0];
        String lastName = fullName.contains(" ") ? fullName.substring(fullName.indexOf(" ") + 1) : "Пользователь";

        // Формируем запрос в Client Service
        ClientRegistrationRequest request = new ClientRegistrationRequest();
        request.setTelegramId(context.getTelegramId());
        request.setTelegramUsername(update.getMessage().getChat().getUserName());
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNumber(phoneNumber);

        try {
            ClientResponse response = clientServiceClient.registerClient(request);

            context.setState(BotState.MAIN_MENU);
            context.setTempRegistrationData(null); // Чистим временные данные
            userContextService.saveUserContext(context);

            responseSender.sendMessage(context.getChatId(),
                    "Регистрация успешна! Добро пожаловать, " + response.getFirstName() + ".",
                    KeyboardFactory.getMainMenuKeyboard());

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "Ошибка регистрации: " + e.getMessage() + ". Попробуйте еще раз.");
        }
    }
}
