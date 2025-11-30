package ru.spbkt.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;
import ru.spbkt.client.service.ClientService;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * [Сценарий 1.1] Регистрация нового клиента.
     * @param request Данные для регистрации (Telegram ID, Имя, Телефон).
     * @return Созданный профиль клиента.
     */
    @PostMapping("/register")
    public ResponseEntity<ClientResponse> registerClient(@RequestBody @Valid ClientRegistrationRequest request) {
        ClientResponse response = clientService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * [Сценарий 5.1] Получение профиля клиента.
     * Используется ботом для отображения данных пользователю.
     * @param telegramId ID клиента в Telegram.
     * @return Профиль клиента.
     */
    @GetMapping("/{telegramId}")
    public ResponseEntity<ClientResponse> getClientProfile(@PathVariable Long telegramId) {
        return ResponseEntity.ok(clientService.getClientProfile(telegramId));
    }

    /**
     * [Сценарий 5.2] Обновление профиля клиента (Имя, Фамилия, Email).
     * @param telegramId ID клиента в Telegram.
     * @param request Обновляемые поля.
     * @return Обновленный профиль клиента.
     */
    @PutMapping("/{telegramId}")
    public ResponseEntity<ClientResponse> updateClientProfile(
            @PathVariable Long telegramId,
            @RequestBody @Valid ClientProfileUpdateRequest request) {

        ClientResponse response = clientService.updateClientProfile(telegramId, request);
        return ResponseEntity.ok(response);
    }
}
