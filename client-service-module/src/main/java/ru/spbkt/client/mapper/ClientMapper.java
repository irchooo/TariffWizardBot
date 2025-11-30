package ru.spbkt.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.spbkt.client.dto.request.ClientProfileUpdateRequest;
import ru.spbkt.client.dto.request.ClientRegistrationRequest;
import ru.spbkt.client.dto.response.ClientResponse;
import ru.spbkt.client.model.Client;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE // Игнорировать null при обновлении
)
public interface ClientMapper {

    // --- Entity -> Response ---

    @Mapping(target = "statusName", source = "status.name")
    ClientResponse toResponse(Client entity);

    // --- Registration Request -> Entity ---

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentTariffId", ignore = true)
    @Mapping(target = "email", ignore = true)
    Client toEntity(ClientRegistrationRequest request);

    // --- Update Request -> Entity (Обновление) ---
    // Обновляет только те поля, которые пришли (не null), в существующей сущности

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "telegramUsername", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true) // Телефон менять через этот метод запрещено
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentTariffId", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ClientProfileUpdateRequest request, @MappingTarget Client entity);
}
