package ru.spbkt.bot.integration;

import ru.spbkt.applications.dto.response.ApplicationResponse;

import java.util.List;

/**
 * Клиент для взаимодействия с модулем управления заявками.
 */
public interface ApplicationServiceClient {

    /**
     * Создание заявки на готовый тариф.
     * @param request Запрос на готовый тариф (FixedTariffApplicationRequest).
     * @return Созданная заявка.
     */
    ApplicationResponse createFixedApplication(Object request); // Object - это FixedTariffApplicationRequest

    /**
     * Создание заявки на кастомный тариф.
     * @param request Запрос на кастомный тариф (CustomTariffApplicationRequest).
     * @return Созданная заявка.
     */
    ApplicationResponse createCustomApplication(Object request); // Object - это CustomTariffApplicationRequest

    /**
     * Получение списка заявок пользователя.
     * @param telegramId ID Телеграм пользователя.
     * @return Список заявок.
     */
    List<ApplicationResponse> getMyApplications(Long telegramId);

    /**
     * Отмена заявки.
     * @param id ID заявки.
     * @param request Причина отмены (ApplicationCancelRequest).
     * @return Обновленная заявка.
     */
    ApplicationResponse cancelApplication(Long id, Object request); // Object - это ApplicationCancelRequest
}
