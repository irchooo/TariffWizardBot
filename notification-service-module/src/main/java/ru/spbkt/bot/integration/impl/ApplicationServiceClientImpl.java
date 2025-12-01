package ru.spbkt.bot.integration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.spbkt.applications.dto.request.ApplicationCancelRequest;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.applications.dto.response.ApplicationResponse;
import ru.spbkt.bot.config.ServiceProperties;
import ru.spbkt.bot.integration.ApplicationServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceClientImpl implements ApplicationServiceClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private String getBaseUrl() {
        return serviceProperties.getApplication().getUrl();
    }

    @Override
    public ApplicationResponse createFixedApplication(Object request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/fixed") // POST /api/v1/applications/fixed
                .toUriString();

        FixedTariffApplicationRequest fixedRequest = (FixedTariffApplicationRequest) request;
        return restTemplate.postForObject(url, fixedRequest, ApplicationResponse.class);
    }

    @Override
    public ApplicationResponse createCustomApplication(Object request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/custom") // POST /api/v1/applications/custom
                .toUriString();

        CustomTariffApplicationRequest customRequest = (CustomTariffApplicationRequest) request;
        return restTemplate.postForObject(url, customRequest, ApplicationResponse.class);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(Long telegramId) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/my/{telegramId}") // GET /api/v1/applications/my/{telegramId}
                .buildAndExpand(telegramId)
                .toUriString();

        // Используем ParameterizedTypeReference для десериализации списка
        ResponseEntity<List<ApplicationResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public ApplicationResponse cancelApplication(Long id, Object request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/{id}/cancel") // POST /api/v1/applications/{id}/cancel
                .buildAndExpand(id)
                .toUriString();

        ApplicationCancelRequest cancelRequest = (ApplicationCancelRequest) request;

        // Используем postForObject, так как это действие с телом запроса
        return restTemplate.postForObject(url, cancelRequest, ApplicationResponse.class);
    }
}
