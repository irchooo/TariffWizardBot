package ru.spbkt.client.service;

import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;

public interface ClientService {

    /**
     * Сценарий 1.1: Регистрация нового клиента.
     * Проверяет на дубликаты и присваивает статус "НОВЫЙ".
     */
    ClientResponse registerClient(ClientRegistrationRequest request);

    /**
     * Сценарий 5.1: Получение профиля клиента по Telegram ID.
     */
    ClientResponse getClientProfile(Long telegramId);

    /**
     * Сценарий 5.2: Обновление имени, фамилии или email клиента.
     */
    ClientResponse updateClientProfile(Long telegramId, ClientProfileUpdateRequest request);

    /**
     * Внутренний метод для проверки существования клиента по Telegram ID.
     * Используется модулями-потребителями.
     */
    boolean clientExistsByTelegramId(Long telegramId);
}
