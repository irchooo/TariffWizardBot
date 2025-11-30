package ru.spbkt.bot.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.tariff.url}")
    private String tariffBaseUrl; // http://localhost:8080/api/v1

    /**
     * Получить список готовых тарифов.
     */
    public List<TariffResponse> getAllTariffs() {
        String url = tariffBaseUrl + "/tariffs";
        // Используем exchange для получения List<T>
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TariffResponse>>() {}
        ).getBody();
    }

    /**
     * Получить параметры для конструктора (ГБ, Минуты).
     */
    public List<ServiceParameterResponse> getConstructorParameters() {
        String url = tariffBaseUrl + "/tariffs/parameters";
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ServiceParameterResponse>>() {}
        ).getBody();
    }

    /**
     * Рассчитать стоимость кастомного тарифа (предпросмотр).
     */
    public TariffCalculationResponse calculateCustomPrice(CustomTariffRequest request) {
        String url = tariffBaseUrl + "/calculation/custom";
        return restTemplate.postForObject(url, request, TariffCalculationResponse.class);
    }
}
