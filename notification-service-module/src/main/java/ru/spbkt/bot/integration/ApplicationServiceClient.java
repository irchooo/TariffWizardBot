package ru.spbkt.bot.integration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.spbkt.applications.dto.request.ApplicationCancelRequest;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.applications.dto.response.ApplicationResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.application.url}")
    private String applicationUrl; // http://localhost:8082/api/v1/applications

    public ApplicationResponse createFixedApplication(FixedTariffApplicationRequest request) {
        String url = applicationUrl + "/fixed";
        return restTemplate.postForObject(url, request, ApplicationResponse.class);
    }

    public ApplicationResponse createCustomApplication(CustomTariffApplicationRequest request) {
        String url = applicationUrl + "/custom";
        return restTemplate.postForObject(url, request, ApplicationResponse.class);
    }

    public List<ApplicationResponse> getMyApplications(Long telegramId) {
        String url = applicationUrl + "/my?telegramId=" + telegramId;
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ApplicationResponse>>() {}
        ).getBody();
    }

    public void cancelApplication(Long applicationId, String reason) {
        String url = applicationUrl + "/" + applicationId + "/cancel";
        ApplicationCancelRequest request = new ApplicationCancelRequest();
        request.setReason(reason);
        restTemplate.postForObject(url, request, ApplicationResponse.class);
    }
}
