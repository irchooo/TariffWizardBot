package ru.spbkt.tariff.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TariffDetailResponse {

    private String parameterName;
    private String unit;
    private Integer volume;
    private BigDecimal priceCoefficient;

}
