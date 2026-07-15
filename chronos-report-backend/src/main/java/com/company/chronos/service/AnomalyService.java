package com.company.chronos.service;

import com.company.chronos.dto.anomaly.AnomalyResponse;
import com.company.chronos.dto.anomaly.ResolveAnomalyRequest;
import com.company.chronos.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for retrieving and resolving cost-allocation anomalies.
 */
public interface AnomalyService {

    /**
     * Returns a paginated list of anomalies for a company, optionally filtered
     * by resolved status.
     *
     * @param companyId the owning company id
     * @param resolved filter on resolved status (null = no filter)
     * @param pageable pagination metadata
     * @return a page of anomaly responses
     */
    PageResponse<AnomalyResponse> getAnomalies(Long companyId, Boolean resolved,
                                              Pageable pageable);

    /**
     * Resolves (or re-opens) an anomaly.
     *
     * @param id the anomaly id
     * @param request the resolve request
     * @return the updated anomaly response
     */
    AnomalyResponse resolveAnomaly(Long id, ResolveAnomalyRequest request);
}