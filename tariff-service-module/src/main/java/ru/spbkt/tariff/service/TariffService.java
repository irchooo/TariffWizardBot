package ru.spbkt.tariff.service;

import ru.spbkt.tariff.dto.response.TariffResponse;

import java.util.List;

public interface TariffService {
    /**
     * Возвращает список всех активных готовых тарифов.
     */
    List<TariffResponse> getAllActiveTariffs();

    /**
     * Возвращает детальную информацию о тарифе по ID.
     */
    TariffResponse getTariffById(Integer id);

}
