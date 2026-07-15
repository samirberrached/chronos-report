package com.company.chronos.controller;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.OrganizationRequest;
import com.company.chronos.dto.company.OrganizationResponse;
import com.company.chronos.service.OrganizationService;
import com.company.chronos.util.PaginationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for {@code Organization} dimension management.
 */
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    /** Organization service. */
    private final OrganizationService organizationService;

    /**
     * Lists organizations for a company (paginated).
     *
     * @param companyId the owning company id
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the sort property
     * @param direction the sort direction
     * @return a page of organization responses
     */
    @GetMapping
    public ResponseEntity<PageResponse<OrganizationResponse>> getOrganizations(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PaginationUtils.toPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(organizationService.getOrganizations(companyId, pageable));
    }

    /**
     * Returns a single organization.
     *
     * @param id the organization id
     * @return the organization response
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getOrganization(@PathVariable Long id) {
        return ResponseEntity.ok(organizationService.getOrganization(id));
    }

    /**
     * Creates a new organization.
     *
     * @param request the organization request
     * @return the created organization response
     */
    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(
            @Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(organizationService.createOrganization(request));
    }

    /**
     * Updates an existing organization.
     *
     * @param id the organization id
     * @param request the updated fields
     * @return the updated organization response
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> updateOrganization(
            @PathVariable Long id, @Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.ok(organizationService.updateOrganization(id, request));
    }

    /**
     * Deletes an organization.
     *
     * @param id the organization id
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.noContent().build();
    }
}