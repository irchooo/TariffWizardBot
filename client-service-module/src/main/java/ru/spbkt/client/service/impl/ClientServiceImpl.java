package ru.spbkt.client.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;
import ru.spbkt.client.exception.ClientAlreadyExistsException;
import ru.spbkt.client.exception.ClientNotFoundException;
import ru.spbkt.client.mapper.ClientMapper;
import ru.spbkt.client.model.Client;
import ru.spbkt.client.model.ClientStatus;
import ru.spbkt.client.repository.ClientRepository;
import ru.spbkt.client.repository.ClientStatusRepository;
import ru.spbkt.client.service.ClientService;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientStatusRepository statusRepository;
    private final ClientMapper clientMapper;

    private static final String NEW_STATUS_NAME = "НОВЫЙ";

    @Override
    @Transactional
    public ClientResponse registerClient(ClientRegistrationRequest request) {
        // 1. Проверка на дубликаты (Telegram ID и Телефон)
        if (clientRepository.existsByTelegramId(request.getTelegramId())) {
            throw new ClientAlreadyExistsException("Клиент с Telegram ID " + request.getTelegramId() + " уже зарегистрирован.");
        }
        if (clientRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ClientAlreadyExistsException("Клиент с номером телефона " + request.getPhoneNumber() + " уже зарегистрирован.");
        }

        // 2. Поиск дефолтного статуса "НОВЫЙ" (Гарантирован Liquibase)
        ClientStatus defaultStatus = statusRepository.findByName(NEW_STATUS_NAME)
                .orElseThrow(() -> new RuntimeException("Начальный статус '" + NEW_STATUS_NAME + "' не найден в БД."));

        // 3. Маппинг и сохранение
        Client client = clientMapper.toEntity(request);
        client.setStatus(defaultStatus);

        Client savedClient = clientRepository.save(client);
        return clientMapper.toResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientProfile(Long telegramId) {
        Client client = clientRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new ClientNotFoundException(telegramId));

        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional
    public ClientResponse updateClientProfile(Long telegramId, ClientProfileUpdateRequest request) {
        // 1. Поиск существующего клиента
        Client client = clientRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new ClientNotFoundException(telegramId));

        // 2. Обновление полей с помощью MapStruct (обновятся только не-null поля)
        clientMapper.updateEntityFromRequest(request, client);

        // 3. Сохранение и возврат
        Client updatedClient = clientRepository.save(client);
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean clientExistsByTelegramId(Long telegramId) {
        return clientRepository.existsByTelegramId(telegramId);
    }
}
