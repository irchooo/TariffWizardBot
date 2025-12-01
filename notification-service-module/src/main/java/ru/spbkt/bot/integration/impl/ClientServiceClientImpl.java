package ru.spbkt.bot.integration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.spbkt.bot.config.ServiceProperties;
import ru.spbkt.bot.integration.ClientServiceClient;
import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;

@Service
@RequiredArgsConstructor
public class ClientServiceClientImpl implements ClientServiceClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private String getBaseUrl() {
        return serviceProperties.getClient().getUrl();
    }

    @Override
    public boolean clientExists(Long telegramId) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/exists/{telegramId}")
                .buildAndExpand(telegramId)
                .toUriString();

        // Предполагается, что эндпоинт возвращает boolean или HttpStatus.OK/NOT_FOUND.
        // Используем String.class для простоты, если сервис возвращает 'true'/'false' как текст.
        // Более надежный способ: try-catch NotFoundException или использование Response Entity.
        try {
            // Используем GET с возвратом boolean, если сервис так настроен (200 true, 404 false)
            return Boolean.TRUE.equals(restTemplate.getForObject(url, Boolean.class));
        } catch (Exception e) {
            // Если получили 404/500, считаем, что клиента нет.
            // В реальном проекте здесь нужно обрабатывать только 404.
            return false;
        }
    }

    @Override
    public ClientResponse registerClient(Object request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/register")
                .toUriString();

        ClientRegistrationRequest registrationRequest = (ClientRegistrationRequest) request;
        return restTemplate.postForObject(url, registrationRequest, ClientResponse.class);
    }

    @Override
    public ClientResponse getClientProfile(Long telegramId) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/{telegramId}")
                .buildAndExpand(telegramId)
                .toUriString();

        return restTemplate.getForObject(url, ClientResponse.class);
    }

    @Override
    public ClientResponse updateClientProfile(Long telegramId, Object request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/{telegramId}")
                .buildAndExpand(telegramId)
                .toUriString();

        ClientProfileUpdateRequest updateRequest = (ClientProfileUpdateRequest) request;

        // Используем exchange с HttpMethod.PUT
        restTemplate.put(url, updateRequest);

        // После PUT принято повторно запросить объект, если PUT возвращает пустое тело (204)
        return getClientProfile(telegramId);
    }
}
