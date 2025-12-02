package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.TariffServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class MenuNavigationHandler implements InputHandler {

    private final ResponseSender responseSender;
    private final TariffServiceClient tariffServiceClient;
    private final ProfileHandler profileHandler;

    // Карта для сопоставления текста кнопки с новым BotState
    private static final Map<String, BotState> COMMAND_TO_STATE = Map.of(
            "🔧 Конструктор", BotState.CONSTRUCTOR_GB,
            "📋 Готовые тарифы", BotState.CATALOG_VIEW,
            "👤 Профиль", BotState.PROFILE_VIEW,
            "📂 Мои заявки", BotState.APPLICATIONS_LIST,
            "📞 Поддержка", BotState.SUPPORT_VIEW
    );

    @Override
    public BotState getHandlerName() {
        return BotState.MAIN_MENU;
    }

    @Override
    public void handle(Update update, UserContext context) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(context.getChatId(), "Пожалуйста, используйте кнопки меню.");
            return;
        }

        String command = update.getMessage().getText();

        // 1. Проверяем, является ли текст командой навигации
        BotState nextState = COMMAND_TO_STATE.get(command);

        if (nextState != null) {
            handleNavigation(context, nextState);
        } else {
            responseSender.sendMessage(context.getChatId(), "Неизвестная команда. Выберите действие из главного меню.", KeyboardFactory.getMainMenuKeyboard());
        }
    }

    private void handleNavigation(UserContext context, BotState nextState) {
        context.setState(nextState);

        switch (nextState) {
            case CONSTRUCTOR_GB:
                // Сценарий 2: Конструктор. Сбрасываем черновик и спрашиваем первый параметр.
                context.setTariffDraft(null); // Очистка черновика

                // TODO: Получить параметры от TariffServiceClient и показать их
                responseSender.sendMessage(context.getChatId(),
                        "***🔧 КОНСТРУКТОР ТАРИФА: ШАГ 1/3 (ИНТЕРНЕТ)***\n" +
                                "Выберите желаемый объем интернет-трафика (ГБ).",
                        KeyboardFactory.getCancelKeyboard()); // Здесь будет Inline/Reply Keyboard с вариантами ГБ
                break;

            case CATALOG_VIEW:
                // Сценарий 3: Каталог.
                // TODO: Получить список тарифов от TariffServiceClient и показать их
                responseSender.sendMessage(context.getChatId(),
                        "***📋 КАТАЛОГ ГОТОВЫХ ТАРИФОВ***\n" +
                                "Загружаю список доступных тарифов...",
                        KeyboardFactory.getCancelKeyboard()); // Здесь будет список тарифов
                break;

            case PROFILE_VIEW:
                // СЦЕНАРИЙ 5: Профиль. ВМЕСТО отправки сообщения, ДЕЛЕГИРУЕМ
                profileHandler.showProfile(context, null); // <--- ПРАВИЛЬНЫЙ ВЫЗОВ
                // ProfileHandler сам установит состояние на PROFILE_VIEW и отправит сообщение с данными.
                break;

            case APPLICATIONS_LIST:
                // Сценарий 6: Заявки. Делегируется ApplicationHandler.
                responseSender.sendMessage(context.getChatId(),
                        "Переход к заявкам...");
                break;

            case SUPPORT_VIEW:
                // Сценарий 7: Поддержка.
                responseSender.sendMessage(context.getChatId(),
                        "***📞 СЛУЖБА ПОДДЕРЖКИ***\n" +
                                "Телефон: +7 (812) 555-0101\n" +
                                "Email: support@spbkt.ru",
                        KeyboardFactory.getMainMenuKeyboard());
                context.setState(BotState.MAIN_MENU); // Вывод информации сразу возвращает в меню
                break;

            default:
                // Все остальные состояния (которые не должны наступить здесь)
                responseSender.sendMessage(context.getChatId(), "Ошибка навигации. Вернитесь в меню.", KeyboardFactory.getMainMenuKeyboard());
                context.setState(BotState.MAIN_MENU);
        }
    }
}
