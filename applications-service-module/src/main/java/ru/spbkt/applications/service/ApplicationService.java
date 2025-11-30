package ru.spbkt.applications.service;

import ru.spbkt.applications.dto.request.ApplicationCancelRequest;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.applications.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {

    /**
     * Создает заявку на готовый тариф (Сценарий 3).
     */
    ApplicationResponse createFixedApplication(FixedTariffApplicationRequest request);

    /**
     * Создает заявку на конструктор (Сценарий 2).
     */
    ApplicationResponse createCustomApplication(CustomTariffApplicationRequest request);

    /**
     * Отменяет заявку (Сценарий 6.1).
     */
    ApplicationResponse cancelApplication(Long applicationId, ApplicationCancelRequest request);

    /**
     * Получает список всех заявок клиента (по Telegram ID).
     */
    List<ApplicationResponse> getMyApplications(Long telegramId);

    /**
     * Получает детали одной заявки.
     */
    ApplicationResponse getApplicationById(Long id);
}
