package com.company.chronos.mapper;

import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.employee.EmployeeResponse;
import com.company.chronos.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper converting between {@link Employee} entities and their
 * request/response DTOs. Component model is "spring" so it is injected like any
 * other bean.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    /**
     * Maps an {@link Employee} entity to its response DTO, denormalizing the
     * owning company and organization names.
     *
     * @param employee the source entity
     * @return the response DTO
     */
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "organization.name", target = "organizationName")
    EmployeeResponse toResponse(Employee employee);

    /**
     * Updates an existing {@link Employee} entity from a request DTO. FK
     * associations (company, organization) are resolved by the service layer
     * and set explicitly, so they are ignored here.
     *
     * @param request the source request
     * @param employee the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);

    /**
     * Creates a new {@link Employee} entity from a request DTO. FK associations
     * are resolved by the service layer and set explicitly, so they are ignored
     * here.
     *
     * @param request the source request
     * @return the new entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModifiedAt", ignore = true)
    Employee toEntity(EmployeeRequest request);
}