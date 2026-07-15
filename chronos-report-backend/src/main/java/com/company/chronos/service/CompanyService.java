package com.company.chronos.service;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.CompanyRequest;
import com.company.chronos.dto.company.CompanyResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for managing {@code Company} dimension records.
 */
public interface CompanyService {

    /**
     * Returns a paginated list of companies.
     *
     * @param pageable pagination metadata
     * @return a page of company responses
     */
    PageResponse<CompanyResponse> getCompanies(Pageable pageable);

    /**
     * Returns a single company by id.
     *
     * @param id the company id
     * @return the company response
     */
    CompanyResponse getCompany(Long id);

    /**
     * Creates a new company.
     *
     * @param request the company request
     * @return the created company response
     */
    CompanyResponse createCompany(CompanyRequest request);

    /**
     * Updates an existing company.
     *
     * @param id the company id
     * @param request the updated fields
     * @return the updated company response
     */
    CompanyResponse updateCompany(Long id, CompanyRequest request);

    /**
     * Deletes a company by id.
     *
     * @param id the company id
     */
    void deleteCompany(Long id);
}