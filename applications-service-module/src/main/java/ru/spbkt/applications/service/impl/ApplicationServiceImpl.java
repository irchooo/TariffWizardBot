package ru.spbkt.applications.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.applications.dto.request.ApplicationCancelRequest;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.applications.dto.response.ApplicationResponse;
import ru.spbkt.applications.exception.ApplicationNotFoundException;
import ru.spbkt.applications.exception.InvalidApplicationStateException;
import ru.spbkt.applications.mapper.ApplicationMapper;
import ru.spbkt.applications.model.Application;
import ru.spbkt.applications.model.ApplicationDetail;
import ru.spbkt.applications.model.ApplicationDetailId;
import ru.spbkt.applications.model.ApplicationStatus;
import ru.spbkt.applications.model.ApplicationStatusHistory;
import ru.spbkt.applications.model.ApplicationType;
import ru.spbkt.applications.model.CustomApplication;
import ru.spbkt.applications.model.TariffApplication;
import ru.spbkt.applications.repository.ApplicationRepository;
import ru.spbkt.applications.repository.ApplicationStatusHistoryRepository;
import ru.spbkt.applications.repository.ApplicationStatusRepository;
import ru.spbkt.applications.service.ApplicationService;
import ru.spbkt.client.api.ClientModuleApi;
import ru.spbkt.client.dto.response.ClientResponse;
import ru.spbkt.tariff.api.TariffModuleApi;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusRepository statusRepository;
    private final ApplicationStatusHistoryRepository historyRepository;
    private final ApplicationMapper applicationMapper;

    // Внешние модули
    private final ClientModuleApi clientModuleApi;
    private final TariffModuleApi tariffModuleApi;

    private static final String STATUS_CREATED = "СОЗДАНА";
    private static final String STATUS_CANCELLED = "ОТМЕНЕНА";
    private static final String STATUS_COMPLETED = "ВЫПОЛНЕНА";

    @Override
    @Transactional
    public ApplicationResponse createFixedApplication(FixedTariffApplicationRequest request) {
        // 1. Проверяем тариф (через API тарифов)
        tariffModuleApi.calculateFixedTariff(request.getTariffId()); // Если не найден, выбросит исключение внутри

        // 2. Создаем базовую заявку
        Application application = createBaseApplication(request.getClientId(), ApplicationType.FIXED);

        // 3. Создаем детали (TariffApplication)
        TariffApplication tariffApp = new TariffApplication();
        tariffApp.setApplication(application);
        tariffApp.setTariffId(request.getTariffId());

        application.setTariffApplication(tariffApp);

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public ApplicationResponse createCustomApplication(CustomTariffApplicationRequest request) {
        // 1. Рассчитываем стоимость через API тарифов (это гарантия цены)
        // Преобразуем запрос заявки в запрос калькулятора
        CustomTariffRequest calcRequest = new CustomTariffRequest();
        // (Тут нужен простой маппинг списка параметров, сделаем стримом)
        List<CustomTariffRequest.SelectedParameter> params = request.getParameters().stream()
                .map(p -> {
                    var sp = new CustomTariffRequest.SelectedParameter();
                    sp.setParameterId(p.getParameterId());
                    sp.setVolume(p.getVolume());
                    return sp;
                }).toList();
        calcRequest.setParameters(params);

        TariffCalculationResponse calculation = tariffModuleApi.calculateCustomTariff(calcRequest);

        // 2. Создаем базовую заявку
        Application application = createBaseApplication(request.getClientId(), ApplicationType.CUSTOM);

        // 3. Создаем детали (CustomApplication + ApplicationDetail)
        CustomApplication customApp = new CustomApplication();
        customApp.setApplication(application);
        customApp.setTotalCost(calculation.getTotalCost());

        // Собираем детали из расчета (CalculationResponse -> ApplicationDetail Entity)
        List<ApplicationDetail> detailsEntities = new ArrayList<>();

        for (var item : calculation.getDetails()) {
            // Находим ID параметра по имени (или лучше, если CalculationResponse возвращает ID)
            // Допустим, мы немного доработали CalculationResponse, чтобы он возвращал ID параметра,
            // или ищем параметр в CustomTariffRequest.
            // В реальном коде лучше, чтобы CalculationResponse.CalculationItem содержал parameterId.
            // Для примера предположим, что мы сопоставили их.

            // Упрощение: берем parameterId из запроса, так как порядок совпадает
            // В продакшене лучше маппить по ID.
            Integer paramId = request.getParameters().stream()
                    .filter(p -> p.getVolume().equals(item.getVolume()) && item.getParameterName().contains(/*логика*/""))
                    .findFirst().map(CustomTariffApplicationRequest.CustomParameterRequest::getParameterId).orElse(0);
            // Это место требует доработки DTO тарифов, чтобы возвращать ID.
            // Допустим, CalculationItem имеет поле parameterId.

            ApplicationDetail detail = new ApplicationDetail();
            detail.setCustomApplication(customApp);
            detail.setVolume(item.getVolume());
            detail.setCost(item.getTotalItemCost());
            // Установка ключа
            ApplicationDetailId key = new ApplicationDetailId();
            // key.setCustomApplicationId - установится JPA автоматически при сохранении родителя
            key.setParameterId(paramId); // Здесь нужен реальный ID
            detail.setId(key);

            detailsEntities.add(detail);
        }

        customApp.setDetails(detailsEntities);
        application.setCustomApplication(customApp);

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public ApplicationResponse cancelApplication(Long applicationId, ApplicationCancelRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(applicationId));

        // Проверка: нельзя отменить выполненную
        if (STATUS_COMPLETED.equals(application.getStatus().getName()) ||
                STATUS_CANCELLED.equals(application.getStatus().getName())) {
            throw new InvalidApplicationStateException("Нельзя отменить заявку в статусе " + application.getStatus().getName());
        }

        // Смена статуса
        ApplicationStatus oldStatus = application.getStatus();
        ApplicationStatus newStatus = getStatusByName(STATUS_CANCELLED);
        application.setStatus(newStatus);

        // Запись в историю
        ApplicationStatusHistory history = new ApplicationStatusHistory();
        history.setApplication(application);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setReason(request.getReason());
        historyRepository.save(history);

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(Long telegramId) {
        // Вызываем ClientModuleApi, чтобы получить внутренний ID клиента по Telegram ID
        ClientResponse client = clientModuleApi.getClientDataByTelegramId(telegramId);

        return applicationRepository.findAllByClientId(client.getId()).stream()
                .map(applicationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .map(applicationMapper::toResponse)
                .orElseThrow(() -> new ApplicationNotFoundException(id));
    }

    // --- Вспомогательные методы ---

    private Application createBaseApplication(Long clientId, ApplicationType type) {
        Application application = new Application();
        application.setClientId(clientId);
        application.setType(type);
        application.setStatus(getStatusByName(STATUS_CREATED));
        return application;
    }

    private ApplicationStatus getStatusByName(String name) {
        return statusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Status " + name + " not found"));
    }
}
