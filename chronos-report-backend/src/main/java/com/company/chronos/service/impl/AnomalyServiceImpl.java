package com.company.chronos.service.impl;

import com.company.chronos.dto.anomaly.AnomalyResponse;
import com.company.chronos.dto.anomaly.ResolveAnomalyRequest;
import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.entity.Company;
import com.company.chronos.entity.EmployeeAnomaly;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.AnomalyMapper;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.repository.EmployeeAnomalyRepository;
import com.company.chronos.service.AnomalyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link AnomalyService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnomalyServiceImpl implements AnomalyService {

    /** Anomaly repository. */
    private final EmployeeAnomalyRepository anomalyRepository;

    /** Company repository. */
    private final CompanyRepository companyRepository;

    /** Anomaly mapper. */
    private final AnomalyMapper anomalyMapper;

    @Override
    public PageResponse<AnomalyResponse> getAnomalies(Long companyId, Boolean resolved,
                                                     Pageable pageable) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        Page<EmployeeAnomaly> page;
        if (resolved == null) {
            page = anomalyRepository.findByCompany(company, pageable);
        } else {
            page = anomalyRepository.findByCompanyAndResolved(company, resolved, pageable);
        }
        return PageResponse.<AnomalyResponse>builder()
                .content(page.getContent().stream().map(anomalyMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional
    public AnomalyResponse resolveAnomaly(Long id, ResolveAnomalyRequest request) {
        EmployeeAnomaly anomaly = anomalyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Anomaly", "id", id));
        anomaly.setResolved(request.getResolved());
        anomaly.setResolutionNote(request.getResolutionNote());
        return anomalyMapper.toResponse(anomalyRepository.save(anomaly));
    }
}