package ru.spbkt.bot.integration.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.spbkt.bot.config.ServiceProperties;
import ru.spbkt.bot.integration.TariffServiceClient;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.List;

/**
 * Реализация клиента для Tariff-Service-Module через HTTP.
 * Использует RestTemplate.
 */
@Service
@RequiredArgsConstructor
public class TariffServiceClientImpl implements TariffServiceClient {

    private final RestTemplate restTemplate;
    private final ServiceProperties serviceProperties;

    private String getBaseUrl() {
        return serviceProperties.getTariff().getUrl();
    }

    @Override
    public List<ServiceParameterResponse> getServiceParameters() {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/catalog/parameters")
                .toUriString();

        // Параметризованный тип для корректной десериализации списка
        ResponseEntity<List<ServiceParameterResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public TariffCalculationResponse calculateCustom(CustomTariffRequest request) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/calculation/custom")
                .toUriString();

        // POST-запрос с телом CustomTariffRequest
        return restTemplate.postForObject(url, request, TariffCalculationResponse.class);
    }

    // ... Реализации getTariffCatalog() и calculateFixed() по аналогии ...

    @Override
    public List<TariffResponse> getTariffCatalog() {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/catalog/tariffs")
                .toUriString();

        ResponseEntity<List<TariffResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public TariffCalculationResponse calculateFixed(Integer tariffId) {
        String url = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .path("/calculation/tariffs/{tariffId}")
                .buildAndExpand(tariffId)
                .toUriString();

        return restTemplate.getForObject(url, TariffCalculationResponse.class);
    }
}
