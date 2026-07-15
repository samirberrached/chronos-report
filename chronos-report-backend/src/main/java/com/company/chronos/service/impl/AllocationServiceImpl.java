package com.company.chronos.service.impl;

import com.company.chronos.dto.allocation.AllocationResponse;
import com.company.chronos.entity.Company;
import com.company.chronos.entity.EmployeeAllocation;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.AllocationMapper;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.repository.EmployeeAllocationRepository;
import com.company.chronos.service.AllocationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AllocationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AllocationServiceImpl implements AllocationService {

    /** Allocation repository. */
    private final EmployeeAllocationRepository allocationRepository;

    /** Company repository. */
    private final CompanyRepository companyRepository;

    /** Allocation mapper. */
    private final AllocationMapper allocationMapper;

    @Override
    public List<AllocationResponse> getAllocations(Long companyId, String month) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        List<EmployeeAllocation> allocations =
                allocationRepository.findByCompanyIdAndMonth(company.getId(), month);
        return allocations.stream().map(allocationMapper::toResponse).toList();
    }
}