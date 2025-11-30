package ru.spbkt.applications.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.spbkt.applications.dto.request.ApplicationCancelRequest;
import ru.spbkt.applications.dto.request.CustomTariffApplicationRequest;
import ru.spbkt.applications.dto.request.FixedTariffApplicationRequest;
import ru.spbkt.applications.dto.response.ApplicationResponse;
import ru.spbkt.applications.service.ApplicationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    // 1. Создание заявки на готовый тариф
    @PostMapping("/fixed")
    public ResponseEntity<ApplicationResponse> createFixed(@RequestBody @Valid FixedTariffApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createFixedApplication(request));
    }

    // 2. Создание заявки на конструктор
    @PostMapping("/custom")
    public ResponseEntity<ApplicationResponse> createCustom(@RequestBody @Valid CustomTariffApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.createCustomApplication(request));
    }

    // 3. Получение списка заявок пользователя (по Telegram ID)
    // Пример вызова: GET /api/v1/applications/my?telegramId=12345
    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(@RequestParam Long telegramId) {
        return ResponseEntity.ok(applicationService.getMyApplications(telegramId));
    }

    // 4. Отмена заявки
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApplicationResponse> cancelApplication(
            @PathVariable Long id,
            @RequestBody @Valid ApplicationCancelRequest request) {
        return ResponseEntity.ok(applicationService.cancelApplication(id, request));
    }

    // 5. Получение деталей конкретной заявки
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }
}
