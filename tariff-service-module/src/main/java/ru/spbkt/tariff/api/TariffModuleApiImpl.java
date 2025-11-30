package ru.spbkt.tariff.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.service.TariffCalculationService;

/**
 * Реализация внутреннего API для модуля тарифов.
 * Является точкой входа для Java-вызовов из других модулей.
 */
@Service // Или @Component. Главное, чтобы Spring его нашел.
@RequiredArgsConstructor
public class TariffModuleApiImpl implements TariffModuleApi {

    private final TariffCalculationService calculationService;

    @Override
    public TariffCalculationResponse calculateFixedTariff(Integer tariffId) {

        //делегируем вызов основному сервису бизнес-логики
        return calculationService.calculateFixedTariff(tariffId);

    }

    @Override
    public TariffCalculationResponse calculateCustomTariff(CustomTariffRequest request) {

        //делегируем вызов основному сервису бизнес-логики
        return calculationService.calculateCustomTariff(request);

    }

}
