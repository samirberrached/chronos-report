package com.company.chronos.service;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.employee.EmployeeResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for managing {@code Employee} dimension records.
 */
public interface EmployeeService {

    /**
     * Returns a paginated list of employees for a company.
     *
     * @param companyId the owning company id
     * @param pageable pagination metadata
     * @return a page of employee responses
     */
    PageResponse<EmployeeResponse> getEmployees(Long companyId, Pageable pageable);

    /**
     * Returns a single employee by id.
     *
     * @param id the employee id
     * @return the employee response
     */
    EmployeeResponse getEmployee(Long id);

    /**
     * Creates a new employee.
     *
     * @param request the employee request
     * @return the created employee response
     */
    EmployeeResponse createEmployee(EmployeeRequest request);

    /**
     * Updates an existing employee.
     *
     * @param id the employee id
     * @param request the updated fields
     * @return the updated employee response
     */
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    /**
     * Deletes an employee by id.
     *
     * @param id the employee id
     */
    void deleteEmployee(Long id);
}