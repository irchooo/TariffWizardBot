package ru.spbkt.tariff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.mapper.TariffMapper;
import ru.spbkt.tariff.repository.ServiceParameterRepository;
import ru.spbkt.tariff.service.ServiceParameterService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceParameterServiceImpl implements ServiceParameterService {

    private final ServiceParameterRepository parameterRepository;
    private final TariffMapper tariffMapper;

    @Override
    @Transactional(readOnly = true) // Оптимизация для операций чтения
    public List<ServiceParameterResponse> getAllActiveParameters() {

        return parameterRepository.findAllByIsActiveTrue().stream()
                .map(tariffMapper::toParameterResponse)
                .toList();

    }

}
