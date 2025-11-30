package ru.spbkt.bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    private KeyboardFactory() {}

    // Главное меню (Reply)
    public static ReplyKeyboardMarkup createMainMenuKeyboard() {
        KeyboardRow row1 = new KeyboardRow(List.of(new KeyboardButton("📊 Каталог"), new KeyboardButton("🛠️ Конструктор")));
        KeyboardRow row2 = new KeyboardRow(List.of(new KeyboardButton("👤 Профиль"), new KeyboardButton("📝 Заявки")));
        KeyboardRow row3 = new KeyboardRow(List.of(new KeyboardButton("📞 Поддержка")));
        return ReplyKeyboardMarkup.builder().keyboard(List.of(row1, row2, row3)).resizeKeyboard(true).build();
    }

    // Запрос контакта (Reply)
    public static ReplyKeyboardMarkup createRequestContactKeyboard() {
        KeyboardButton btn = KeyboardButton.builder().text("📱 Отправить номер").requestContact(true).build();
        return ReplyKeyboardMarkup.builder().keyboard(List.of(new KeyboardRow(List.of(btn)))).resizeKeyboard(true).oneTimeKeyboard(true).build();
    }

    // Список тарифов (Inline)
    public static InlineKeyboardMarkup createTariffListKeyboard(List<TariffResponse> tariffs) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (TariffResponse t : tariffs) {
            rows.add(List.of(InlineKeyboardButton.builder()
                    .text(t.getName() + " (" + t.getPrice() + "₽)")
                    .callbackData("TARIFF_" + t.getId())
                    .build()));
        }
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    // Список параметров для конструктора (Inline)
    // parameterId нужен, чтобы знать, что именно мы выбираем (1=ГБ, 2=Мин)
    public static InlineKeyboardMarkup createParameterSelectionKeyboard(List<ServiceParameterResponse> parameters, int currentParameterId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // Фильтруем только нужный тип параметра (например, только опции Интернета)
        parameters.stream()
                .filter(p -> p.getId().equals(currentParameterId)) // В реальной жизни ID могут отличаться, тут упрощение
                .findFirst()
                .ifPresent(param -> {
                    // Здесь логика должна быть сложнее: ServiceParameterResponse должен содержать список доступных объемов (options).
                    // Предположим для упрощения, что мы генерируем кнопки 5, 10, 15... на основе логики или данных
                    // В MVP можно захардкодить или передавать список Options.
                });

        // ЗАГЛУШКА ДЛЯ MVP: Генерируем варианты на лету
        int step = (currentParameterId == 1) ? 10 : 100; // 10 ГБ или 100 Мин
        String unit = (currentParameterId == 1) ? "Гб" : "Мин";

        for (int i = 1; i <= 4; i++) {
            int val = i * step;
            rows.add(List.of(InlineKeyboardButton.builder()
                    .text(val + " " + unit)
                    .callbackData("PARAM_" + currentParameterId + "_" + val) // PARAM_1_10
                    .build()));
        }
        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    // Подтверждение заказа (Inline)
    public static InlineKeyboardMarkup createConfirmOrderKeyboard() {
        return InlineKeyboardMarkup.builder().keyboard(List.of(
                List.of(InlineKeyboardButton.builder().text("✅ Оформить").callbackData("ORDER_CONFIRM").build()),
                List.of(InlineKeyboardButton.builder().text("❌ Отмена").callbackData("ORDER_CANCEL").build())
        )).build();
    }
}
