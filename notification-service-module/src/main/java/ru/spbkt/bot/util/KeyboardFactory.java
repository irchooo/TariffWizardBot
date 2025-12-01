package ru.spbkt.bot.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

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
