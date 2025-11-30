package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrationHandler implements InputHandler {

    private final ClientServiceClient clientService;
    private final ResponseSender responseSender;

    @Override
    public List<BotState> getSupportedStates() {
        return List.of(BotState.START, BotState.WAITING_NAME, BotState.WAITING_LAST_NAME, BotState.WAITING_PHONE);
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals("/start")) {
            checkRegistration(update, context);
            return;
        }

        if (context.getCurrentState() == BotState.WAITING_NAME) {
            context.getDraft().setClientFirstName(update.getMessage().getText());
            context.setCurrentState(BotState.WAITING_LAST_NAME);
            responseSender.sendMessage(context.getChatId(), "Введите вашу фамилию:");
        } else if (context.getCurrentState() == BotState.WAITING_LAST_NAME) {
            context.getDraft().setClientLastName(update.getMessage().getText());
            context.setCurrentState(BotState.WAITING_PHONE);

            SendMessage msg = new SendMessage(context.getChatId().toString(), "Поделитесь номером телефона, используя кнопку ниже:");
            msg.setReplyMarkup(KeyboardFactory.createRequestContactKeyboard());
            responseSender.sendMessage(msg);
        } else if (context.getCurrentState() == BotState.WAITING_PHONE) {
            if (update.getMessage().hasContact()) {
                String phone = update.getMessage().getContact().getPhoneNumber();
                // Нормализация номера (добавить + если нет)
                if (!phone.startsWith("+")) phone = "+" + phone;

                context.getDraft().setClientPhoneNumber(phone);
                completeRegistration(context);
            } else {
                responseSender.sendMessage(context.getChatId(), "Пожалуйста, используйте кнопку для отправки контакта.");
            }
        }
    }

    private void checkRegistration(Update update, UserContext context) {
        Long telegramId = update.getMessage().getFrom().getId();
        var clientOpt = clientService.getClientByTelegramId(telegramId);

        if (clientOpt.isPresent()) {
            context.setClientId(clientOpt.get().getId());
            context.setCurrentState(BotState.MAIN_MENU);
            sendMainMenu(context.getChatId(), "С возвращением, " + clientOpt.get().getFirstName() + "!");
        } else {
            context.setCurrentState(BotState.WAITING_NAME);
            responseSender.sendMessage(context.getChatId(), "Добро пожаловать! Давайте зарегистрируемся.\nВведите ваше имя:");
        }
    }

    private void completeRegistration(UserContext context) {
        ClientRegistrationRequest req = new ClientRegistrationRequest();
        req.setTelegramId(context.getChatId()); // В Telegram ID чата и юзера совпадают для ЛС
        req.setFirstName(context.getDraft().getClientFirstName());
        req.setLastName(context.getDraft().getClientLastName());
        req.setPhoneNumber(context.getDraft().getClientPhoneNumber());

        try {
            var client = clientService.registerClient(req);
            context.setClientId(client.getId());
            context.setCurrentState(BotState.MAIN_MENU);
            sendMainMenu(context.getChatId(), "Регистрация успешна! Выберите действие в меню.");
        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "Ошибка регистрации: " + e.getMessage());
            context.setCurrentState(BotState.START);
        }
    }

    private void sendMainMenu(Long chatId, String text) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        msg.setReplyMarkup(KeyboardFactory.createMainMenuKeyboard());
        responseSender.sendMessage(msg);
    }
}
