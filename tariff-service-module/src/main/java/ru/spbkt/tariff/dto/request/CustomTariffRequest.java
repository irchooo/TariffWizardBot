package ru.spbkt.tariff.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.List;

@Data
public class CustomTariffRequest {

    @Valid
    @NotNull
    private List<SelectedParameter> parameters;

    @Data
    public static class SelectedParameter {

        @NotNull(message = "ID параметра обязателен")
        private Integer parameterId;

        @NotNull
        @PositiveOrZero(message = "Объем не может быть отрицательным")
        private Integer volume;
    }

}
