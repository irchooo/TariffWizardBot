package ru.spbkt.tariff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.dto.response.TariffDetailResponse;
import ru.spbkt.tariff.dto.response.TariffResponse;
import ru.spbkt.tariff.model.ServiceParameter;
import ru.spbkt.tariff.model.Tariff;
import ru.spbkt.tariff.model.TariffDetail;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TariffMapper {

    ServiceParameterResponse toParameterResponse(ServiceParameter entity);

    List<ServiceParameterResponse> toParameterResponseList(List<ServiceParameter> entities);

    TariffResponse toTariffResponse(Tariff entity);

    List<TariffResponse> toTariffResponseList(List<Tariff> entities);

    @Mapping(target = "parameterName", source = "parameter.name")
    @Mapping(target = "unit", source = "parameter.unit")
    TariffDetailResponse toDetailResponse(TariffDetail entity);
}
