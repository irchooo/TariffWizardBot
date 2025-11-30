package ru.spbkt.tariff.service;

import ru.spbkt.tariff.dto.response.ServiceParameterResponse;

import java.util.List;

public interface ServiceParameterService {
    /**
     * Возвращает список активных параметров (Интернет, Минуты) для конструктора.
     */
    List<ServiceParameterResponse> getAllActiveParameters();

}
