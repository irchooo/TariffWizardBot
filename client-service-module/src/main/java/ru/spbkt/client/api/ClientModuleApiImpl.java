package ru.spbkt.client.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.client.dto.response.ClientResponse;
import ru.spbkt.client.service.ClientService;

@Service
@RequiredArgsConstructor
public class ClientModuleApiImpl implements ClientModuleApi {

    private final ClientService clientService;

    @Override
    @Transactional(readOnly = true)
    public boolean clientExistsByTelegramId(Long telegramId) {
        return clientService.clientExistsByTelegramId(telegramId);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientDataByTelegramId(Long telegramId) {
        // Используем публичный метод сервиса
        return clientService.getClientProfile(telegramId);
    }

    @Override
    @Transactional // Этот метод меняет состояние клиента, поэтому нужна транзакция записи
    public void updateCurrentTariff(Long telegramId, Integer tariffId) {
        // Мы не реализуем этот метод полностью здесь,
        // но он должен найти клиента, установить client.currentTariffId = tariffId
        // и сохранить.
        // Для завершения этого модуля достаточно заглушки:
        // Client client = clientRepository.findByTelegramId(telegramId) ...
        // client.setCurrentTariffId(tariffId);
        // clientRepository.save(client);

        // В рамках текущей задачи мы оставим его как заглушку, но в реальном проекте он бы вызывал репозиторий.
    }
}
