package com.company.chronos.mapper;

import com.company.chronos.dto.report.ReportResponse;
import com.company.chronos.entity.EmployeeReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link EmployeeReport} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface ReportMapper {

    /**
     * Maps an {@link EmployeeReport} entity to its response DTO, denormalizing
     * the owning company name.
     *
     * @param report the source entity
     * @return the response DTO
     */
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    ReportResponse toResponse(EmployeeReport report);
}