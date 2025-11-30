package ru.spbkt.applications.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.spbkt.tariff.api.TariffModuleApi;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

@Service
@RequiredArgsConstructor
public class TariffModuleApiHttpImpl implements TariffModuleApi {

    private final RestTemplate restTemplate;

    @Value("${services.tariff.url}")
    private String tariffServiceUrl; // http://localhost:8080/api/v1/calculation

    @Override
    public TariffCalculationResponse calculateFixedTariff(Integer tariffId) {
        // GET http://localhost:8080/api/v1/calculation/tariffs/{tariffId}
        String url = tariffServiceUrl + "/tariffs/" + tariffId;
        return restTemplate.getForObject(url, TariffCalculationResponse.class);
    }

    @Override
    public TariffCalculationResponse calculateCustomTariff(CustomTariffRequest request) {
        // POST http://localhost:8080/api/v1/calculation/custom
        String url = tariffServiceUrl + "/custom";
        return restTemplate.postForObject(url, request, TariffCalculationResponse.class);
    }
}
