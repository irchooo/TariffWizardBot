package ru.spbkt.bot.integration;

import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.List;

/**
 * Клиент для взаимодействия с модулем тарифов и расчета цен.
 */
public interface TariffServiceClient {

    /**
     * Возвращает список активных параметров для конструктора (Гб, Мин, СМС)
     * @return Список параметров.
     */
    List<ServiceParameterResponse> getServiceParameters();

    /**
     * Рассчитывает стоимость кастомного тарифа (предпросмотр).
     * @param request Объемы выбранных параметров (Гб, Мин, СМС).
     * @return Детализация расчета и итоговая стоимость[cite: 1160].
     */
    TariffCalculationResponse calculateCustom(CustomTariffRequest request);

    /**
     * Возвращает список активных готовых тарифов.
     * @return Список тарифов.
     */
    List<TariffResponse> getTariffCatalog();

    /**
     * Рассчитывает стоимость готового тарифа по ID (чек).
     * @param tariffId ID готового тарифа.
     * @return Детализация расчета и итоговая стоимость[cite: 1158].
     */
    TariffCalculationResponse calculateFixed(Integer tariffId);
    // <-- ДОБАВИТЬ ЭТОТ МЕТОД

}
