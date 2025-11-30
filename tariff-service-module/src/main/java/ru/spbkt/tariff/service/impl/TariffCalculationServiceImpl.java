package ru.spbkt.tariff.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.spbkt.tariff.dto.request.CustomTariffRequest;
import ru.spbkt.tariff.dto.response.TariffCalculationResponse;
import ru.spbkt.tariff.exception.ServiceParameterNotFoundException;
import ru.spbkt.tariff.exception.TariffNotFoundException;
import ru.spbkt.tariff.model.ServiceParameter;
import ru.spbkt.tariff.model.Tariff;
import ru.spbkt.tariff.model.TariffDetail;
import ru.spbkt.tariff.repository.ServiceParameterRepository;
import ru.spbkt.tariff.repository.TariffRepository;
import ru.spbkt.tariff.service.TariffCalculationService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffCalculationServiceImpl implements TariffCalculationService {

    private final ServiceParameterRepository parameterRepository;
    private final TariffRepository tariffRepository;

    //1. Расчет для Конструктора
    @Override
    @Transactional(readOnly = true)
    public TariffCalculationResponse calculateCustomTariff(CustomTariffRequest request) {

        List<TariffCalculationResponse.CalculationItem> details = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CustomTariffRequest.SelectedParameter item : request.getParameters()) {

            //поиск параметра в БД (чтобы узнать его цену)
            ServiceParameter param = parameterRepository.findById(item.getParameterId())
                    .orElseThrow(() -> new ServiceParameterNotFoundException(item.getParameterId()));

            //считаем: цена = объем * макс цена
            BigDecimal itemCost = param.getMaxPricePerUnit()
                    .multiply(BigDecimal.valueOf(item.getVolume()));

            totalCost = totalCost.add(itemCost);

            //добавление строчки в детализацию
            details.add(TariffCalculationResponse.CalculationItem.builder()
                    .parameterId(param.getId())
                    .parameterName(param.getName())
                    .unit(param.getUnit())
                    .volume(item.getVolume())
                    .pricePerUnit(param.getMaxPricePerUnit())
                    .totalItemCost(itemCost)
                    .build());

        }

        return TariffCalculationResponse.builder()
                .totalCost(totalCost)
                .isCustom(true)
                .details(details)
                .build();

    }

    //2. Расчет для Готового тарифа
    @Override
    @Transactional(readOnly = true)
    public TariffCalculationResponse calculateFixedTariff(Integer tariffId) {

        Tariff tariff = tariffRepository.findById(tariffId)
                .orElseThrow(() -> new TariffNotFoundException(tariffId));

        List<TariffCalculationResponse.CalculationItem> details = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        //проход по деталям тарифа (связка параметр + объем + коэффициент)
        for (TariffDetail detail : tariff.getDetails()) {

            ServiceParameter param = detail.getParameter();

            //эффективная цена = базовая цена * коэффициент
            BigDecimal effectivePrice = param.getMaxPricePerUnit()
                    .multiply(detail.getPriceCoefficient());

            //стоимость позиции = эффективная цена * объем
            BigDecimal itemCost = effectivePrice
                    .multiply(BigDecimal.valueOf(detail.getVolume()));

            totalCost = totalCost.add(itemCost);

            details.add(TariffCalculationResponse.CalculationItem.builder()
                    .parameterId(param.getId())
                    .parameterName(param.getName())
                    .unit(param.getUnit())
                    .volume(detail.getVolume())
                    .pricePerUnit(effectivePrice) //цена уже со скидкой для клиента
                    .totalItemCost(itemCost)
                    .build());

        }

        return TariffCalculationResponse.builder()
                .totalCost(totalCost)
                .isCustom(false)
                .details(details)
                .build();

    }

}