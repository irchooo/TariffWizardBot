package ru.spbkt.tariff.service;

import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

public interface TariffCalculationService {
    /**
     * Рассчитывает стоимость кастомного тарифа на основе выбранных параметров.
     */
    TariffCalculationResponse calculateCustomTariff(CustomTariffRequest request);

    /**
     * Рассчитывает стоимость готового тарифа (нужно для отображения чека перед покупкой).
     */
    TariffCalculationResponse calculateFixedTariff(Integer tariffId);

}
