package ru.spbkt.client.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClientRegistrationRequest {

    @NotNull(message = "Telegram ID обязателен")
    private Long telegramId;

    private String telegramUsername;

    @NotBlank(message = "Имя обязательно")
    @Size(min = 1, max = 50)
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Size(min = 1, max = 50)
    private String lastName;

    @NotBlank(message = "Номер телефона обязателен")
    @Size(min = 10, max = 20, message = "Некорректная длина номера")
    // Простая проверка: только цифры, плюс, скобки и дефисы
    @Pattern(regexp = "^[+0-9\\-\\(\\) ]+$", message = "Некорректный формат номера")
    private String phoneNumber;
}
