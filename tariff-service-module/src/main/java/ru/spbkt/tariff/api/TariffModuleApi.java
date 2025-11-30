package ru.spbkt.tariff.api;

import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;

/**
 * Публичный контракт модуля тарифов для взаимодействия с другими модулями (Java-to-Java).
 * Исключает необходимость в HTTP-запросах внутри монолита.
 */
public interface TariffModuleApi {

    /**
     * Рассчитывает и возвращает стоимость готового тарифа по его ID.
     * Используется модулем заявок для финального чека.
     * @param tariffId ID готового тарифа.
     * @return Детализация расчета и итоговая стоимость.
     */
    TariffCalculationResponse calculateFixedTariff(Integer tariffId);

    /**
     * Рассчитывает и возвращает стоимость кастомного тарифа на основе выбранных параметров.
     * Используется модулем заявок при создании тарифа через конструктор.
     * @param request Объемы выбранных параметров.
     * @return Детализация расчета и итоговая стоимость.
     */
    TariffCalculationResponse calculateCustomTariff(CustomTariffRequest request);

}
