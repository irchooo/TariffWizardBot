package ru.spbkt.applications.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.applications.dto.response.ApplicationResponse;
import ru.spbkt.applications.service.ApplicationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationModuleApiImpl implements ApplicationModuleApi {

    private final ApplicationService applicationService;

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getActiveApplicationsByTelegramId(Long telegramId) {
        // Делегируем вызов сервису
        List<ApplicationResponse> allApps = applicationService.getMyApplications(telegramId);

        // Фильтруем только активные (СОЗДАНА, В_ОБРАБОТКЕ)
        return allApps.stream()
                .filter(app -> "СОЗДАНА".equals(app.getStatusName()) || "В_ОБРАБОТКЕ".equals(app.getStatusName()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingApplication(Long telegramId) {
        return !getActiveApplicationsByTelegramId(telegramId).isEmpty();
    }
}
