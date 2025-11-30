package ru.spbkt.applications.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplicationDetailResponse {
    private Integer parameterId; // ID услуги (Интернет/Минуты)
    private Integer volume;      // Объем
    private BigDecimal cost;     // Стоимость этой позиции
}
