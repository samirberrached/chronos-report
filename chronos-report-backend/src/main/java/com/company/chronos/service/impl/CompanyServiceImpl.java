package com.company.chronos.service.impl;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.CompanyRequest;
import com.company.chronos.dto.company.CompanyResponse;
import com.company.chronos.entity.Company;
import com.company.chronos.exception.DuplicateResourceException;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.CompanyMapper;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link CompanyService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompanyServiceImpl implements CompanyService {

    /** Company repository. */
    private final CompanyRepository companyRepository;

    /** Company mapper. */
    private final CompanyMapper companyMapper;

    @Override
    public PageResponse<CompanyResponse> getCompanies(Pageable pageable) {
        Page<Company> page = companyRepository.findAll(pageable);
        return PageResponse.<CompanyResponse>builder()
                .content(page.getContent().stream().map(companyMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public CompanyResponse getCompany(Long id) {
        return companyMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        if (companyRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException(
                    "Company", "companyCode", request.getCompanyCode());
        }
        Company company = companyMapper.toEntity(request);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = findById(id);
        if (!company.getCompanyCode().equals(request.getCompanyCode())
                && companyRepository.existsByCompanyCode(request.getCompanyCode())) {
            throw new DuplicateResourceException(
                    "Company", "companyCode", request.getCompanyCode());
        }
        companyMapper.updateEntityFromRequest(request, company);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.delete(findById(id));
    }

    /**
     * Loads a company by id or throws.
     */
    private Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }
}