package ru.spbkt.applications.api;

import ru.spbkt.applications.dto.response.ApplicationResponse;

import java.util.List;

/**
 * Публичный контракт модуля заявок.
 */
public interface ApplicationModuleApi {

    /**
     * Получить все активные заявки клиента.
     * Может пригодиться модулю Бот для проверки состояния.
     */
    List<ApplicationResponse> getActiveApplicationsByTelegramId(Long telegramId);

    /**
     * Проверка, есть ли у клиента незавершенная заявка (чтобы не создавать вторую).
     */
    boolean hasPendingApplication(Long telegramId);
}
