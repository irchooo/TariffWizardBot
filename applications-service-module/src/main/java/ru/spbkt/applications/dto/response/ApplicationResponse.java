package ru.spbkt.applications.dto.response;

import lombok.Data;
import ru.spbkt.applications.model.ApplicationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApplicationResponse {

    private Long id;
    private Long clientId;
    private String statusName;      // "СОЗДАНА", "ВЫПОЛНЕНА"
    private ApplicationType type;   // FIXED, CUSTOM
    private LocalDateTime createdAt;

    // --- Поля для готового тарифа (заполнены, если type == FIXED) ---
    private Integer tariffId;

    // --- Поля для конструктора (заполнены, если type == CUSTOM) ---
    private BigDecimal totalCost;
    private List<ApplicationDetailResponse> details;
}
