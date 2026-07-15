package com.company.chronos.service;

import com.company.chronos.dto.allocation.AllocationResponse;
import java.util.List;

/**
 * Service contract for retrieving cost-allocation fact rows.
 */
public interface AllocationService {

    /**
     * Returns all allocations for a company and reporting month.
     *
     * @param companyId the owning company id
     * @param month the reporting month (yyyy-MM)
     * @return the list of allocation responses
     */
    List<AllocationResponse> getAllocations(Long companyId, String month);
}