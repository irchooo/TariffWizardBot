package ru.spbkt.client.api;

import ru.spbkt.client.dto.response.ClientResponse;

/**
 * Публичный контракт модуля клиентов для Java-to-Java взаимодействия.
 */
public interface ClientModuleApi {

    /**
     * Проверяет, существует ли клиент с данным Telegram ID.
     */
    boolean clientExistsByTelegramId(Long telegramId);

    /**
     * Получает полную информацию о клиенте по его Telegram ID.
     */
    ClientResponse getClientDataByTelegramId(Long telegramId);

    // Дополнительные методы, которые могут потребоваться другим модулям (например, смена тарифа)
    void updateCurrentTariff(Long telegramId, Integer tariffId);
}
