package com.company.chronos.mapper;

import com.company.chronos.dto.allocation.AllocationResponse;
import com.company.chronos.entity.EmployeeAllocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for {@link EmployeeAllocation} entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AllocationMapper {

    /**
     * Maps an {@link EmployeeAllocation} entity to its response DTO,
     * denormalizing the related dimension names.
     *
     * @param allocation the source entity
     * @return the response DTO
     */
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.fullName", target = "employeeName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "activity.id", target = "activityId")
    @Mapping(source = "activity.name", target = "activityName")
    @Mapping(source = "accountingCode.id", target = "accountingCodeId")
    @Mapping(source = "accountingCode.code", target = "accountingCode")
    AllocationResponse toResponse(EmployeeAllocation allocation);
}