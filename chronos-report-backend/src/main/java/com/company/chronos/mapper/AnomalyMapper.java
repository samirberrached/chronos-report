package com.company.chronos.mapper;

import com.company.chronos.dto.anomaly.AnomalyResponse;
import com.company.chronos.entity.EmployeeAnomaly;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link EmployeeAnomaly} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AnomalyMapper {

    /**
     * Maps an {@link EmployeeAnomaly} entity to its response DTO, denormalizing
     * the related employee name and report id.
     *
     * @param anomaly the source entity
     * @return the response DTO
     */
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.fullName", target = "employeeName")
    @Mapping(source = "report.id", target = "reportId")
    AnomalyResponse toResponse(EmployeeAnomaly anomaly);
}