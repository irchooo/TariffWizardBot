package ru.spbkt.tariff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.service.TariffCalculationService;

@RestController
@RequestMapping("/api/v1/calculation")
@RequiredArgsConstructor
public class TariffCalculationController {

    private final TariffCalculationService calculationService;

    /**
     * Расчет стоимости для Конструктора (пользователь выбрал объемы сам).
     */
    @PostMapping("/custom")
    public ResponseEntity<TariffCalculationResponse> calculateCustom(@RequestBody @Valid CustomTariffRequest request) {

        return ResponseEntity.ok(calculationService.calculateCustomTariff(request));

    }

    /**
     * Получение расчета стоимости для Готового тарифа (чек).
     */
    @GetMapping("/tariffs/{tariffId}")
    public ResponseEntity<TariffCalculationResponse> calculateFixed(@PathVariable Integer tariffId) {

        return ResponseEntity.ok(calculationService.calculateFixedTariff(tariffId));

    }

}
