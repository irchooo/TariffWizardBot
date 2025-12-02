package ru.spbkt.bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeyboardFactory {

    public static ReplyKeyboardMarkup getRegistrationKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton("Поделиться контактом");
        button.setRequestContact(true); // Важно: запрашиваем номер телефона
        row.add(button);

        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboard = new ArrayList<>();

        // Ряд 1: Основные действия
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("🔧 Конструктор"));
        row1.add(new KeyboardButton("📋 Готовые тарифы"));

        // Ряд 2: Личный кабинет
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("👤 Профиль"));
        row2.add(new KeyboardButton("📂 Мои заявки"));

        // Ряд 3: Инфо
        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("📞 Поддержка"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static ReplyKeyboardMarkup getProfileMenuKeyboard() {
        List<String> buttons = List.of(
                "✍️ Изменить Имя",
                "✍️ Изменить Фамилию",
                "⬅️ В главное меню"
        );
        // Две кнопки в ряду для первых двух команд, третья отдельно
        return createReplyKeyboard(buttons, 2);
    }

    // --- 1. Вспомогательный метод для создания ReplyKeyboardMarkup ---
    /**
     * Создает стандартную ReplyKeyboardMarkup.
     * @param buttons Список текстов кнопок.
     * @param buttonsPerRow Максимальное количество кнопок в одном ряду.
     * @return ReplyKeyboardMarkup.
     */
    private static ReplyKeyboardMarkup createReplyKeyboard(List<String> buttons, int buttonsPerRow) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        int counter = 0;
        for (String buttonText : buttons) {
            row.add(new KeyboardButton(buttonText));
            counter++;

            if (counter >= buttonsPerRow) {
                keyboard.add(row);
                row = new KeyboardRow();
                counter = 0;
            }
        }

        // Добавляем последний неполный ряд
        if (!row.isEmpty()) {
            keyboard.add(row);
        }

        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    // --- 3. Вспомогательные методы для Inline-клавиатур ---

    /**
     * Создает Inline-клавиатуру из Map<Текст кнопки, callbackData>.
     * @param options Карта опций.
     * @param buttonsPerRow Количество кнопок в ряду.
     * @return InlineKeyboardMarkup.
     */
    public static InlineKeyboardMarkup createInlineKeyboard(Map<String, String> options, int buttonsPerRow) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        List<String> keys = new ArrayList<>(options.keySet());

        for (int i = 0; i < keys.size(); i += buttonsPerRow) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < buttonsPerRow && i + j < keys.size(); j++) {
                String text = keys.get(i + j);
                String callbackData = options.get(text);
                row.add(createCallbackButton(text, callbackData));
            }
            keyboard.add(row);
        }
        markup.setKeyboard(keyboard);
        return markup;
    }

    /**
     * Создает одну Inline-кнопку.
     */
    public static InlineKeyboardButton createCallbackButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }

    // Клавиатура для отмены действия (возврат в меню)
    public static ReplyKeyboardMarkup getCancelKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("❌ Отмена"));
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
}
