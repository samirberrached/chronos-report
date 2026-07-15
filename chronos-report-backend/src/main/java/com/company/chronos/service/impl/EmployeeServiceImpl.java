package com.company.chronos.service.impl;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.employee.EmployeeResponse;
import com.company.chronos.entity.Company;
import com.company.chronos.entity.Employee;
import com.company.chronos.entity.Organization;
import com.company.chronos.exception.DuplicateResourceException;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.EmployeeMapper;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.repository.EmployeeRepository;
import com.company.chronos.repository.OrganizationRepository;
import com.company.chronos.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link EmployeeService}. Orchestrates the
 * repository, mapper and validation, never returning entities to callers.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    /** Employee repository. */
    private final EmployeeRepository employeeRepository;

    /** Company repository (for FK resolution). */
    private final CompanyRepository companyRepository;

    /** Organization repository (for FK resolution). */
    private final OrganizationRepository organizationRepository;

    /** Employee mapper. */
    private final EmployeeMapper employeeMapper;

    @Override
    public PageResponse<EmployeeResponse> getEmployees(Long companyId, Pageable pageable) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        Page<Employee> page = employeeRepository.findByCompany(company, pageable);
        return PageResponse.<EmployeeResponse>builder()
                .content(page.getContent().stream().map(employeeMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public EmployeeResponse getEmployee(Long id) {
        return employeeMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmployeeCodeAndCompanyId(
                request.getEmployeeCode(), request.getCompanyId())) {
            throw new DuplicateResourceException(
                    "Employee", "employeeCode", request.getEmployeeCode());
        }
        Employee employee = employeeMapper.toEntity(request);
        employee.setCompany(requireCompany(request.getCompanyId()));
        employee.setOrganization(requireOrganization(request.getOrganizationId()));
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findById(id);
        if (!employee.getEmployeeCode().equals(request.getEmployeeCode())
                && employeeRepository.existsByEmployeeCodeAndCompanyId(
                        request.getEmployeeCode(), request.getCompanyId())) {
            throw new DuplicateResourceException(
                    "Employee", "employeeCode", request.getEmployeeCode());
        }
        employeeMapper.updateEntityFromRequest(request, employee);
        employee.setCompany(requireCompany(request.getCompanyId()));
        employee.setOrganization(requireOrganization(request.getOrganizationId()));
        return employeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.delete(findById(id));
    }

    /**
     * Loads an employee by id or throws.
     */
    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    /**
     * Loads a company by id or throws.
     */
    private Company requireCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }

    /**
     * Loads an organization by id or throws.
     */
    private Organization requireOrganization(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
    }
}