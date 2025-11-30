package ru.spbkt.tariff.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceParameterResponse {

    private Integer id;
    private String name;
    private String unit;
    private BigDecimal maxPricePerUnit;
    private String description;

}
