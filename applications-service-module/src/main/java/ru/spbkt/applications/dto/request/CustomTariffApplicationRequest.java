package ru.spbkt.applications.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class CustomTariffApplicationRequest {

    @NotNull(message = "ID клиента обязателен")
    @Positive
    private Long clientId;

    @NotEmpty(message = "Список параметров не может быть пустым")
    @Valid
    private List<CustomParameterRequest> parameters;

    @Data
    public static class CustomParameterRequest {

        @NotNull(message = "ID параметра обязателен")
        private Integer parameterId;

        @NotNull
        @PositiveOrZero(message = "Объем услуги не может быть отрицательным")
        private Integer volume;
    }
}
