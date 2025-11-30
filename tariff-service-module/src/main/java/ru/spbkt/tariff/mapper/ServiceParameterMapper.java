package ru.spbkt.tariff.mapper;

import org.mapstruct.Mapper;
import ru.spbkt.tariff.dto.response.ServiceParameterResponse;
import ru.spbkt.tariff.model.ServiceParameter;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceParameterMapper {

    ServiceParameterResponse toResponse(ServiceParameter entity);

    List<ServiceParameterResponse> toResponseList(List<ServiceParameter> entities);

}
