package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.bot.integration.TariffServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TariffCatalogHandler implements InputHandler {

    private final ResponseSender responseSender;
    private final TariffServiceClient tariffServiceClient;
    private final ClientServiceClient clientServiceClient;

    // Ключ для временного хранения ID тарифа в контексте между шагами
    private static final String TEMP_TARIFF_ID_KEY = "tempTariffId";

    @Override
    public BotState getHandlerName() {
        return BotState.CATALOG_VIEW;
    }

    @Override
    public void handle(Update update, UserContext context) {

        if (update.hasCallbackQuery()) {
            // Обработка Inline-кнопки (выбор тарифа)
            handleTariffSelection(update.getCallbackQuery().getData(), context);
        } else {
            // Если пришли обычным сообщением, но мы в CATALOG_VIEW
            showTariffCatalog(context, "Пожалуйста, выберите тариф из списка ниже.");
        }
    }

    // --- Шаг 1: Отображение каталога ---
    public void showTariffCatalog(UserContext context, String message) {
        try {
            // Получаем список активных тарифов из tariff-service
            List<TariffResponse> tariffs = tariffServiceClient.getAvailableTariffs();

            if (tariffs.isEmpty()) {
                responseSender.sendMessage(context.getChatId(), "К сожалению, сейчас нет доступных тарифов.");
                context.setState(BotState.MAIN_MENU);
                return;
            }

            String listMessage = "***📖 КАТАЛОГ ГОТОВЫХ ТАРИФОВ***\n\n" +
                    tariffs.stream()
                            .map(this::formatTariffForList)
                            .collect(Collectors.joining("\n---\n"));

            // Создаем Inline-клавиатуру для выбора
            Map<String, String> options = tariffs.stream()
                    .collect(Collectors.toMap(
                            t -> "✅ " + t.getName(),             // Текст кнопки: "✅ Название тарифа"
                            t -> "SELECT_" + t.getId()           // Callback: "SELECT_1"
                    ));

            context.setState(BotState.CATALOG_VIEW);

            String finalMessage = (message != null ? message + "\n\n" : "") + listMessage;
            responseSender.sendInlineKeyboard(context.getChatId(), finalMessage, KeyboardFactory.createInlineKeyboard(options, 1));

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(), "❌ Ошибка загрузки каталога: " + e.getMessage(), KeyboardFactory.getMainMenuKeyboard());
            context.setState(BotState.MAIN_MENU);
        }
    }

    private String formatTariffForList(TariffResponse tariff) {
        return String.format(
                "**%s** (%.2f ₽/мес)\n" +
                        "  *Описание:* %s",
                tariff.getName(),
                tariff.getBasePrice(),
                tariff.getDescription()
        );
    }

    // --- Шаг 2: Обработка выбора тарифа ---
    private void handleTariffSelection(String callbackData, UserContext context) {
        if (callbackData.startsWith("SELECT_")) {
            try {
                Long tariffId = Long.parseLong(callbackData.substring(7));

                // Получаем полную информацию о тарифе для подтверждения
                TariffResponse tariff = tariffServiceClient.getTariffById(tariffId);

                // Временно сохраняем ID выбранного тарифа в контекст
                context.getTempData().put(TEMP_TARIFF_ID_KEY, tariffId);

                String confirmationMessage = String.format(
                        "***✅ ВЫБОР ТАРИФА***\n\n" +
                                "Вы выбрали тариф **%s**.\n" +
                                "Стоимость: **%.2f ₽/мес**.\n" +
                                "Описание: %s\n\n" +
                                "Вы хотите подать заявку на подключение этого тарифа?",
                        tariff.getName(),
                        tariff.getBasePrice(),
                        tariff.getDescription());

                // Создаем Inline-клавиатуру для подтверждения
                Map<String, String> options = new HashMap<>();
                options.put("🔥 ПОДТВЕРДИТЬ", "CONFIRM_FIXED");
                options.put("↩️ ВЕРНУТЬСЯ в каталог", "BACK_TO_CATALOG");

                context.setState(BotState.WAITING_TARIFF_CONFIRMATION);
                responseSender.sendInlineKeyboard(context.getChatId(), confirmationMessage, KeyboardFactory.createInlineKeyboard(options, 1));

            } catch (Exception e) {
                showTariffCatalog(context, "❌ Ошибка при загрузке тарифа. Попробуйте снова.");
            }
        } else if (callbackData.equals("BACK_TO_CATALOG")) {
            // Возврат к списку
            context.getTempData().remove(TEMP_TARIFF_ID_KEY);
            showTariffCatalog(context, "Возвращаемся в каталог.");
        }
    }
}
