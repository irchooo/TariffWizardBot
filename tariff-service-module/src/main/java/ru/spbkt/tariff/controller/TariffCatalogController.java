package ru.spbkt.tariff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;
import ru.spbkt.tariff.service.ServiceParameterService;
import ru.spbkt.tariff.service.TariffService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
public class TariffCatalogController {

    private final TariffService tariffService;
    private final ServiceParameterService serviceParameterService;

    // --- Готовые тарифы ---
    @GetMapping
    public ResponseEntity<List<TariffResponse>> getAllTariffs() {

        return ResponseEntity.ok(tariffService.getAllActiveTariffs());

    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffResponse> getTariffById(@PathVariable Integer id) {

        return ResponseEntity.ok(tariffService.getTariffById(id));

    }

    // --- Параметры для конструктора ---
    @GetMapping("/parameters")
    public ResponseEntity<List<ServiceParameterResponse>> getAllParameters() {

        return ResponseEntity.ok(serviceParameterService.getAllActiveParameters());

    }

}
