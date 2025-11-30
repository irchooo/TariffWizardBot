package ru.spbkt.tariff.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class TariffCalculationResponse {

    private BigDecimal totalCost;
    private boolean isCustom;
    private List<CalculationItem> details;

    @Data
    @Builder
    public static class CalculationItem {

        private Integer parameterId;
        private String parameterName;
        private Integer volume;
        private String unit;
        private BigDecimal pricePerUnit;
        private BigDecimal totalItemCost;
    }

}
