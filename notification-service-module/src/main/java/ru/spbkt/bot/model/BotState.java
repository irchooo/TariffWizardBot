package ru.spbkt.bot.model;

/**
 * Перечисление, представляющее текущее состояние пользователя в диалоге с ботом.
 */
public enum BotState {
    START,                  // Начальное состояние /start
    WAITING_NAME,           // Ожидание имени для регистрации
    WAITING_PHONE,          // Ожидание телефона для регистрации
    MAIN_MENU,              // Главное меню

    // Конструктор тарифа (Сценарий 2)
    CONSTRUCTOR_GB,         // Выбор объема интернет-трафика (Гб)
    CONSTRUCTOR_MINUTES,    // Выбор объема минут
    CONSTRUCTOR_SMS,        // Выбор объема СМС
    CONSTRUCTOR_PREVIEW,    // Предпросмотр цены и подтверждение (перед отправкой заявки)

    // Каталог тарифов (Сценарий 3)
    CATALOG_VIEW,           // Просмотр списка готовых тарифов
    WAITING_TARIFF_CONFIRMATION,

    // Профиль (Сценарий 5)
    PROFILE_VIEW,           // Просмотр профиля
    WAITING_FIRST_NAME,     // Ожидание ввода нового имени
    WAITING_LAST_NAME,      // Ожидание ввода новой фамилии

    // Заявки (Сценарий 6)
    APPLICATIONS_LIST,      // Просмотр списка заявок
    APPLICATION_CANCEL_REASON, // Ожидание причины отмены

    // Поддержка (Сценарий 7)
    SUPPORT_VIEW            // Просмотр контактов
}