package com.company.chronos.mapper;

import com.company.chronos.dto.company.OrganizationRequest;
import com.company.chronos.dto.company.OrganizationResponse;
import com.company.chronos.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for {@link Organization} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    /**
     * Maps an {@link Organization} entity to its response DTO, denormalizing
     * the owning company and parent names.
     *
     * @param organization the source entity
     * @return the response DTO
     */
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "parent.name", target = "parentName")
    OrganizationResponse toResponse(Organization organization);

    /**
     * Creates a new {@link Organization} entity from a request DTO. FK
     * associations are resolved by the service layer and set explicitly.
     *
     * @param request the source request
     * @return the new entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    Organization toEntity(OrganizationRequest request);

    /**
     * Updates an existing {@link Organization} entity from a request DTO.
     *
     * @param request the source request
     * @param organization the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    void updateEntityFromRequest(OrganizationRequest request,
                                 @MappingTarget Organization organization);
}