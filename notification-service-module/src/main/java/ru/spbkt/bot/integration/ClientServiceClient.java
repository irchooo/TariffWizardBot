package ru.spbkt.bot.integration;

import ru.spbkt.client.dto.response.ClientResponse;

/**
 * Клиент для взаимодействия с модулем управления клиентами.
 */
public interface ClientServiceClient {
    /**
     * Проверяет, зарегистрирован ли клиент.
     * @param telegramId ID Телеграм пользователя.
     * @return true, если клиент существует.
     */
    boolean clientExists(Long telegramId);

    /**
     * Регистрирует нового клиента.
     * @param request Запрос на регистрацию (имя, фамилия, телефон и т.д.).
     * @return Ответ с данными созданного клиента.
     */
    ClientResponse registerClient(Object request); // Object - это ClientRegistrationRequest

    /**
     * Получает профиль клиента.
     * @param telegramId ID Телеграм пользователя.
     * @return Ответ с данными профиля клиента.
     */
    ClientResponse getClientProfile(Long telegramId);

    /**
     * Обновляет профиль клиента (имя/фамилия).
     * @param telegramId ID Телеграм пользователя.
     * @param request Запрос на обновление.
     * @return Обновленный профиль клиента.
     */
    ClientResponse updateClientProfile(Long telegramId, Object request); // Object - это ClientProfileUpdateRequest
}
