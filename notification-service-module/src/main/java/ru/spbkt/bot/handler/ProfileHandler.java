package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.response.ClientResponse;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfileHandler implements InputHandler {

    private final ResponseSender responseSender;
    private final ClientServiceClient clientServiceClient;

    @Override
    public BotState getHandlerName() {
        return BotState.PROFILE_VIEW;
    }

    @Override
    public void handle(Update update, UserContext context) {

        // 1. Обработка команды "❌ Отмена"
        if (handleCancelCommand(update, context)) {
            return;
        }

        if (!update.hasMessage() || !update.getMessage().hasText()) {
            // Игнорируем нетекстовые сообщения, если не в состоянии ввода
            if (context.getState() == BotState.PROFILE_VIEW) {
                showProfile(context, "Пожалуйста, используйте кнопки меню.");
            }
            return;
        }

        String text = update.getMessage().getText().trim();

        switch (context.getState()) {
            case PROFILE_VIEW:
                // Обработка команд меню профиля
                handleProfileMenuCommand(text, context);
                break;

            case WAITING_FIRST_NAME:
                // Ожидаем ввод нового имени и делаем API-запрос
                processFirstNameUpdate(text, context);
                break;

            case WAITING_LAST_NAME:
                // Ожидаем ввод новой фамилии и делаем API-запрос
                processLastNameUpdate(text, context);
                break;

            default:
                showProfile(context, "Неизвестная команда.");
        }
    }

    // --- Общие вспомогательные методы ---

    private boolean handleCancelCommand(Update update, UserContext context) {
        String command = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            command = update.getMessage().getText();
        }

        if ("❌ Отмена".equals(command)) {
            // Возвращаемся в меню просмотра профиля
            showProfile(context, "Действие отменено.");
            return true;
        }

        return false;
    }

    private void handleFinish(UserContext context, String message, BotState state) {
        context.setState(state);
        responseSender.sendMessage(context.getChatId(),
                message != null ? message : "Возврат в главное меню.",
                KeyboardFactory.getMainMenuKeyboard());
    }

    // --- СЦЕНАРИЙ: Просмотр (PROFILE_VIEW) ---
    public void showProfile(UserContext context, String message) {
        try {
            ClientResponse client = clientServiceClient.getClientProfile(context.getTelegramId());

            String profileInfo = String.format(
                    "***👤 ВАШ ПРОФИЛЬ***\n\n" +
                            "Имя: **%s**\n" +
                            "Фамилия: **%s**\n" +
                            "Телефон: `%s`\n" +
                            "Статус: **%s**\n",
                    Optional.ofNullable(client.getFirstName()).orElse("N/A"),
                    Optional.ofNullable(client.getLastName()).orElse("N/A"),
                    client.getPhoneNumber(),
                    client.getStatusName());

            String finalMessage = (message != null ? message + "\n\n" : "") + profileInfo;

            context.setState(BotState.PROFILE_VIEW);
            responseSender.sendMessage(context.getChatId(), finalMessage, KeyboardFactory.getProfileMenuKeyboard());

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "❌ Ошибка при загрузке профиля: " + e.getMessage());
            handleFinish(context, null, BotState.MAIN_MENU);
        }
    }

    private void handleProfileMenuCommand(String command, UserContext context) {
        if ("✍️ Изменить Имя".equals(command)) {
            context.setState(BotState.WAITING_FIRST_NAME);
            responseSender.sendMessage(context.getChatId(),
                    "***✏️ РЕДАКТИРОВАНИЕ ИМЕНИ***\nВведите новое Имя.\nИли нажмите \"❌ Отмена\".",
                    KeyboardFactory.getCancelKeyboard());
        } else if ("✍️ Изменить Фамилию".equals(command)) {
            context.setState(BotState.WAITING_LAST_NAME);
            responseSender.sendMessage(context.getChatId(),
                    "***✏️ РЕДАКТИРОВАНИЕ ФАМИЛИИ***\nВведите новую Фамилию.\nИли нажмите \"❌ Отмена\".",
                    KeyboardFactory.getCancelKeyboard());
        } else if ("⬅️ В главное меню".equals(command)) {
            handleFinish(context, "Возврат в главное меню.", BotState.MAIN_MENU);
        } else {
            showProfile(context, "Неизвестная команда. Пожалуйста, выберите действие.");
        }
    }

    // --- СЦЕНАРИЙ: Изменение Имени (WAITING_FIRST_NAME) ---
    private void processFirstNameUpdate(String firstName, UserContext context) {
        if (firstName.length() < 2 || firstName.length() > 50) {
            responseSender.sendMessage(context.getChatId(),
                    "Имя должно содержать от 2 до 50 символов. Попробуйте еще раз:",
                    KeyboardFactory.getCancelKeyboard());
            return;
        }

        // 🚨 Только Имя. Фамилия передается как null, чтобы не обновляться.
        ClientProfileUpdateRequest request = new ClientProfileUpdateRequest(firstName, null, null);

        try {
            clientServiceClient.updateClientProfile(context.getTelegramId(), request);
            showProfile(context, "✅ Имя успешно обновлено!");

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "❌ Ошибка обновления имени: " + e.getMessage());
            showProfile(context, null);
        }
    }

    // --- СЦЕНАРИЙ: Изменение Фамилии (WAITING_LAST_NAME) ---
    private void processLastNameUpdate(String lastName, UserContext context) {
        if (lastName.length() < 2 || lastName.length() > 50) {
            responseSender.sendMessage(context.getChatId(),
                    "Фамилия должна содержать от 2 до 50 символов. Попробуйте еще раз:",
                    KeyboardFactory.getCancelKeyboard());
            return;
        }

        // 🚨 Только Фамилия. Имя передается как null, чтобы не обновляться.
        ClientProfileUpdateRequest request = new ClientProfileUpdateRequest(null, lastName, null);

        try {
            clientServiceClient.updateClientProfile(context.getTelegramId(), request);
            showProfile(context, "✅ Фамилия успешно обновлена!");

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "❌ Ошибка обновления фамилии: " + e.getMessage());
            showProfile(context, null);
        }
    }
}
