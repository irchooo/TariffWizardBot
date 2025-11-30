package ru.spbkt.applications.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FixedTariffApplicationRequest {

    @NotNull(message = "ID клиента обязателен")
    @Positive
    private Long clientId;

    @NotNull(message = "ID тарифа обязателен")
    @Positive
    private Integer tariffId;
}
