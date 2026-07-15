package com.company.chronos.service;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.OrganizationRequest;
import com.company.chronos.dto.company.OrganizationResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for managing {@code Organization} dimension records.
 */
public interface OrganizationService {

    /**
     * Returns a paginated list of organizations for a company.
     *
     * @param companyId the owning company id
     * @param pageable pagination metadata
     * @return a page of organization responses
     */
    PageResponse<OrganizationResponse> getOrganizations(Long companyId, Pageable pageable);

    /**
     * Returns a single organization by id.
     *
     * @param id the organization id
     * @return the organization response
     */
    OrganizationResponse getOrganization(Long id);

    /**
     * Creates a new organization.
     *
     * @param request the organization request
     * @return the created organization response
     */
    OrganizationResponse createOrganization(OrganizationRequest request);

    /**
     * Updates an existing organization.
     *
     * @param id the organization id
     * @param request the updated fields
     * @return the updated organization response
     */
    OrganizationResponse updateOrganization(Long id, OrganizationRequest request);

    /**
     * Deletes an organization by id.
     *
     * @param id the organization id
     */
    void deleteOrganization(Long id);
}