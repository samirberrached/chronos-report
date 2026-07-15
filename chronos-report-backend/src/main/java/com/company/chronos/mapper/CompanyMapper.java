package com.company.chronos.mapper;

import com.company.chronos.dto.company.CompanyRequest;
import com.company.chronos.dto.company.CompanyResponse;
import com.company.chronos.entity.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for {@link Company} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {

    /**
     * Maps a {@link Company} entity to its response DTO.
     *
     * @param company the source entity
     * @return the response DTO
     */
    CompanyResponse toResponse(Company company);

    /**
     * Creates a new {@link Company} entity from a request DTO.
     *
     * @param request the source request
     * @return the new entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    Company toEntity(CompanyRequest request);

    /**
     * Updates an existing {@link Company} entity from a request DTO.
     *
     * @param request the source request
     * @param company the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    void updateEntityFromRequest(CompanyRequest request, @MappingTarget Company company);
}