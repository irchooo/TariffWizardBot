package ru.spbkt.applications.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.spbkt.applications.dto.response.ApplicationDetailResponse;
import ru.spbkt.applications.dto.response.ApplicationResponse;
import ru.spbkt.applications.model.Application;
import ru.spbkt.applications.model.ApplicationDetail;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    // --- Entity -> Response ---

    @Mapping(target = "statusName", source = "status.name")

    // Маппинг данных для готового тарифа (если есть)
    // MapStruct сам проверит на null: если tariffApplication == null, то tariffId будет null
    @Mapping(target = "tariffId", source = "tariffApplication.tariffId")

    // Маппинг данных для конструктора (если есть)
    @Mapping(target = "totalCost", source = "customApplication.totalCost")
    @Mapping(target = "details", source = "customApplication.details")
    ApplicationResponse toResponse(Application entity);

    List<ApplicationResponse> toResponseList(List<Application> entities);


    // --- Внутренний маппинг деталей ---
    // Вызывается автоматически для списка details

    @Mapping(target = "parameterId", source = "id.parameterId") // Вытаскиваем из составного ключа
    @Mapping(target = "volume", source = "volume")
    @Mapping(target = "cost", source = "cost")
    ApplicationDetailResponse toDetailResponse(ApplicationDetail detail);


    /* Примечание: Методы toEntity (Request -> Entity) здесь не пишем.
       Создание заявки - это сложный процесс, включающий создание связанных таблиц
       (OneToOne, OneToMany) и расчет цен. Это проще и надежнее сделать
       в ApplicationServiceImpl вручную, чем настраивать сложный маппер.
    */
}
