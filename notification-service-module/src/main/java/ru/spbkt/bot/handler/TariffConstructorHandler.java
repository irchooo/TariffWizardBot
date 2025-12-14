package ru.spbkt.bot.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.spbkt.bot.integration.tariff.TariffServiceClient;
import ru.spbkt.bot.model.BotState;
import ru.spbkt.bot.model.TariffDraft;
import ru.spbkt.bot.model.UserContext;
import ru.spbkt.bot.service.ResponseSender;
import ru.spbkt.bot.util.KeyboardFactory;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.request.CustomTariffRequest.SelectedParameter; // Вложенный класс
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TariffConstructorHandler implements InputHandler {

    private final ResponseSender responseSender;
    private final TariffServiceClient tariffServiceClient;

    // --- АРХИТЕКТУРНОЕ ДОПУЩЕНИЕ: ФИКСИРОВАННЫЕ ID УСЛУГ В DB ---
    private static final Integer FIXED_INTERNET_ID = 101;
    private static final Integer FIXED_MINUTES_ID = 102;
    private static final Integer FIXED_SMS_ID = 103;

    // Регулярное выражение для проверки ввода (только цифры)
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+$");

    @Override
    public BotState getHandlerName() {
        return BotState.CONSTRUCTOR_START; // Запускаем через это состояние
    }

    @Override
    public void handle(Update update, UserContext context) {

        // 1. Обработка /cancel
        if (handleCancelCommand(update, context)) {
            return;
        }

        // Мы ожидаем только текст-сообщение от пользователя
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(context.getChatId(), "Пожалуйста, введите числовое значение.",
                    KeyboardFactory.getCancelKeyboard());
            return;
        }

        String text = update.getMessage().getText().trim();
        Integer inputVolume = parseVolume(text);

        if (inputVolume == null) {
            sendInvalidInputMessage(context);
            return;
        }

        // 2. Логика пошагового ввода
        switch (context.getState()) {
            case WAITING_GB_INPUT:
                processGbInput(inputVolume, context);
                break;
            case WAITING_MINUTES_INPUT:
                processMinutesInput(inputVolume, context);
                break;
            case WAITING_SMS_INPUT:
                processSmsInput(inputVolume, context);
                break;
            default:
                // Если сюда попали, значит, ошибка логики
                handleCancel(context);
        }
    }

    // --- Методы UI (Вызываются из MenuNavigationHandler) ---
    public void askForGb(UserContext context) {
        responseSender.sendMessage(context.getChatId(),
                "***🔧 КОНСТРУКТОР ТАРИФА: ШАГ 1/3 (ИНТЕРНЕТ)***\n" +
                        "Введите желаемое количество Гигабайт (ГБ):",
                KeyboardFactory.getCancelKeyboard());
    }

    // --- Вспомогательные методы ---
    private Integer parseVolume(String text) {
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        if (matcher.matches()) {
            try {
                // Мы не позволяем ввод слишком больших чисел или 0
                int volume = Integer.parseInt(text);
                return (volume > 0 && volume < 500) ? volume : null;
            } catch (NumberFormatException ignored) { }
        }
        return null;
    }

    private void sendInvalidInputMessage(UserContext context) {
        String currentStep = "";
        switch (context.getState()) {
            case WAITING_GB_INPUT: currentStep = "ГБ"; break;
            case WAITING_MINUTES_INPUT: currentStep = "Минут"; break;
            case WAITING_SMS_INPUT: currentStep = "СМС"; break;
        }
        responseSender.sendMessage(context.getChatId(),
                "⚠️ Некорректный ввод. Пожалуйста, введите целое число для количества " + currentStep + " (от 1 до 500).",
                KeyboardFactory.getCancelKeyboard());
    }

    private boolean handleCancelCommand(Update update, UserContext context) {
        String command = null;
        if (update.hasCallbackQuery()) {
            command = update.getCallbackQuery().getData();
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            command = update.getMessage().getText();
        }

        // Обработка /cancel (Inline) и "❌ Отмена" (Reply)
        if ("/cancel".equals(command) || "❌ Отмена".equals(command)) {
            handleCancel(context);
            return true;
        }
        return false;
    }

    private void handleCancel(UserContext context) {
        context.setState(BotState.MAIN_MENU);
        context.setTariffDraft(null);
        responseSender.sendMessage(context.getChatId(),
                "Вы вернулись в главное меню.",
                KeyboardFactory.getMainMenuKeyboard());
    }

    // --- Пошаговая логика обработки ---

    private void processGbInput(Integer gb, UserContext context) {
        context.getTariffDraft().setInternetGb(gb);
        context.setState(BotState.WAITING_MINUTES_INPUT);
        responseSender.sendMessage(context.getChatId(),
                "***🔧 КОНСТРУКТОР ТАРИФА: ШАГ 2/3 (МИНИУТЫ)***\n" +
                        "Введите желаемое количество минут:",
                KeyboardFactory.getCancelKeyboard());
    }

    private void processMinutesInput(Integer minutes, UserContext context) {
        context.getTariffDraft().setMinutes(minutes);
        context.setState(BotState.WAITING_SMS_INPUT);
        responseSender.sendMessage(context.getChatId(),
                "***🔧 КОНСТРУКТОР ТАРИФА: ШАГ 3/3 (СМС)***\n" +
                        "Введите желаемое количество СМС:",
                KeyboardFactory.getCancelKeyboard());
    }

    private void processSmsInput(Integer sms, UserContext context) {
        context.getTariffDraft().setSms(sms);
        context.setState(BotState.CONSTRUCTOR_PREVIEW);
        showCalculationPreview(context);
    }

    // --- Шаг 4: Расчет и Предпросмотр ---
    private void showCalculationPreview(UserContext context) {
        TariffDraft draft = context.getTariffDraft();

        // 🚨 ГЛАВНОЕ ИЗМЕНЕНИЕ: Формируем запрос с ФИКСИРОВАННЫМИ ID
        List<SelectedParameter> parameters = List.of(
                new SelectedParameter(FIXED_INTERNET_ID, draft.getInternetGb()),
                new SelectedParameter(FIXED_MINUTES_ID, draft.getMinutes()),
                new SelectedParameter(FIXED_SMS_ID, draft.getSms())
        );

        CustomTariffRequest request = new CustomTariffRequest(parameters);

        try {
            TariffCalculationResponse calculation = tariffServiceClient.calculateCustomTariff(request);

            // Формирование и отправка сообщения (логика из предыдущих итераций)
            String previewMessage = formatPreviewMessage(draft, calculation);

            context.setState(BotState.MAIN_MENU); // После показа предпросмотра возвращаем в главное меню
            responseSender.sendMessage(context.getChatId(),
                    previewMessage,
                    KeyboardFactory.getConstructorPreviewKeyboard());

        } catch (Exception e) {
            responseSender.sendMessage(context.getChatId(),
                    "Ошибка расчета тарифа. Убедитесь, что ID услуг " +
                            "(" + FIXED_INTERNET_ID + ", " + FIXED_MINUTES_ID + ", " + FIXED_SMS_ID +
                            ") существуют в базе tariff-service: " + e.getMessage());
            handleCancel(context);
        }
    }

    private String formatPreviewMessage(TariffDraft draft, TariffCalculationResponse calculation) {
        String details = calculation.getDetails().stream()
                .map(d -> String.format("- %s: %s ₽", d.getDescription(), d.getPrice().toString()))
                .reduce("", (acc, item) -> acc + item + "\n");

        return String.format(
                "***✅ ТАРИФ ГОТОВ К ОФОРМЛЕНИЮ***\n\n" +
                        "**Вы выбрали:**\n" +
                        "| Параметр | Объём |\n" +
                        "|:---|:---|\n" +
                        "| Интернет | %d ГБ |\n" +
                        "| Минуты | %d мин |\n" +
                        "| СМС | %d шт |\n\n" +
                        "**Детализация:**\n%s\n" +
                        "**Итоговая стоимость:** **%s ₽/мес**",
                draft.getInternetGb(),
                draft.getMinutes(),
                draft.getSms(),
                details.isEmpty() ? "Детали не предоставлены." : details,
                calculation.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString()
        );
    }
}
