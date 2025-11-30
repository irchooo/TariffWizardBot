package ru.spbkt.applications.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.spbkt.client.api.ClientModuleApi;
import ru.spbkt.client.dto.response.ClientResponse;

@Service
@RequiredArgsConstructor
public class ClientModuleApiHttpImpl implements ClientModuleApi {

    private final RestTemplate restTemplate;

    // Читаем URL из настроек (см. application.yml ниже)
    @Value("${services.client.url}")
    private String clientServiceUrl; // например: http://localhost:8081/api/v1/clients

    @Override
    public boolean clientExistsByTelegramId(Long telegramId) {
        try {
            // Пытаемся получить профиль. Если 200 OK - клиент есть.
            restTemplate.getForEntity(clientServiceUrl + "/" + telegramId, ClientResponse.class);
            return true;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw ex; // Другие ошибки пробрасываем дальше
        }
    }

    @Override
    public ClientResponse getClientDataByTelegramId(Long telegramId) {
        // GET http://localhost:8081/api/v1/clients/{telegramId}
        return restTemplate.getForObject(clientServiceUrl + "/" + telegramId, ClientResponse.class);
    }

    @Override
    public void updateCurrentTariff(Long telegramId, Integer tariffId) {
        // В ClientController мы пока не сделали отдельный метод для этого,
        // но предполагается PUT /api/v1/clients/{telegramId}/tariff
        // Для MVP оставим пока пустым или реализуем позже, когда допишем контроллер клиентов.
    }
}
