package ru.spbkt.tariff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.tariff.dto.response.TariffResponse;
import ru.spbkt.tariff.exception.TariffNotFoundException;
import ru.spbkt.tariff.mapper.TariffMapper;
import ru.spbkt.tariff.repository.TariffRepository;
import ru.spbkt.tariff.service.TariffService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffServiceImpl implements TariffService {

    private final TariffRepository tariffRepository;
    private final TariffMapper tariffMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TariffResponse> getAllActiveTariffs() {

        return tariffRepository.findAllByIsActiveTrue().stream()
                .map(tariffMapper::toTariffResponse)
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public TariffResponse getTariffById(Integer id) {

        return tariffRepository.findById(id)
                .map(tariffMapper::toTariffResponse)
                .orElseThrow(() -> new TariffNotFoundException(id));

    }

}
