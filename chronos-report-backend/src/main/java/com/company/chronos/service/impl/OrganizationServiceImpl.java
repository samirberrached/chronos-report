package com.company.chronos.service.impl;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.OrganizationRequest;
import com.company.chronos.dto.company.OrganizationResponse;
import com.company.chronos.entity.Company;
import com.company.chronos.entity.Organization;
import com.company.chronos.exception.DuplicateResourceException;
import com.company.chronos.exception.ResourceNotFoundException;
import com.company.chronos.mapper.OrganizationMapper;
import com.company.chronos.repository.CompanyRepository;
import com.company.chronos.repository.OrganizationRepository;
import com.company.chronos.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link OrganizationService}.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationServiceImpl implements OrganizationService {

    /** Organization repository. */
    private final OrganizationRepository organizationRepository;

    /** Company repository (for FK resolution). */
    private final CompanyRepository companyRepository;

    /** Organization mapper. */
    private final OrganizationMapper organizationMapper;

    @Override
    public PageResponse<OrganizationResponse> getOrganizations(Long companyId,
                                                               Pageable pageable) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));
        Page<Organization> page = organizationRepository
                .findAllByCompany(company).stream()
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(
                                list, pageable, list.size())));
        return PageResponse.<OrganizationResponse>builder()
                .content(page.getContent().stream()
                        .map(organizationMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public OrganizationResponse getOrganization(Long id) {
        return organizationMapper.toResponse(findById(id));
    }

    @Override
    @Transactional
    public OrganizationResponse createOrganization(OrganizationRequest request) {
        if (organizationRepository.findAllByCompany(requireCompany(request.getCompanyId()))
                .stream().anyMatch(o -> o.getOrganizationCode()
                        .equals(request.getOrganizationCode()))) {
            throw new DuplicateResourceException("Organization", "organizationCode",
                    request.getOrganizationCode());
        }
        Organization organization = organizationMapper.toEntity(request);
        organization.setCompany(requireCompany(request.getCompanyId()));
        organization.setParent(request.getParentId() != null
                ? findById(request.getParentId()) : null);
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    @Transactional
    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Organization organization = findById(id);
        organizationMapper.updateEntityFromRequest(request, organization);
        organization.setCompany(requireCompany(request.getCompanyId()));
        organization.setParent(request.getParentId() != null
                ? findById(request.getParentId()) : null);
        return organizationMapper.toResponse(organizationRepository.save(organization));
    }

    @Override
    @Transactional
    public void deleteOrganization(Long id) {
        organizationRepository.delete(findById(id));
    }

    /**
     * Loads an organization by id or throws.
     */
    private Organization findById(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
    }

    /**
     * Loads a company by id or throws.
     */
    private Company requireCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
    }
}