package ru.spbkt.bot.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.client.url}")
    private String clientUrl; // http://localhost:8081/api/v1/clients

    /**
     * Поиск клиента по Telegram ID.
     * Возвращает Optional.empty(), если клиент не найден (404).
     */
    public Optional<ClientResponse> getClientByTelegramId(Long telegramId) {
        try {
            ClientResponse response = restTemplate.getForObject(clientUrl + "/" + telegramId, ClientResponse.class);
            return Optional.ofNullable(response);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            }
            log.error("Error getting client profile: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Регистрация клиента.
     */
    public ClientResponse registerClient(ClientRegistrationRequest request) {
        String url = clientUrl + "/register";
        return restTemplate.postForObject(url, request, ClientResponse.class);
    }
}
