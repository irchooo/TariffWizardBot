package ru.spbkt.bot.model;

public enum BotState {
    START,                  // Начальное состояние /start
    MAIN_MENU,              // Главное меню

    // Сценарий 1: Регистрация
    WAITING_NAME,           // Ожидание имени
    WAITING_LAST_NAME,      // Ожидание фамилии
    WAITING_PHONE,          // Ожидание телефона

    // Сценарий 2: Конструктор
    CONSTRUCTOR_START,      // Начало конструктора
    CONSTRUCTOR_GB,         // Ожидание ГБ
    CONSTRUCTOR_MINUTES,    // Ожидание минут
    CONSTRUCTOR_SMS,        // Ожидание СМС
    CONSTRUCTOR_REVIEW,     // Просмотр и подтверждение

    // Сценарий 3: Каталог тарифов
    CATALOG_VIEW,           // Просмотр каталога
    CATALOG_SELECT_FIXED,   // Выбор конкретного тарифа

    // Сценарий 6: Заявки
    APPLICATIONS_LIST,      // Список заявок
    APPLICATION_CANCELLATION_REASON, // Ожидание причины отмены

    // Общее состояние
    AWAITING_INPUT_DEFAULT; // Дефолтное состояние, ожидание текста/команды
}
