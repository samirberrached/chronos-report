package com.company.chronos.controller;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.company.CompanyRequest;
import com.company.chronos.dto.company.CompanyResponse;
import com.company.chronos.service.CompanyService;
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
 * REST controller for {@code Company} dimension management.
 */
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    /** Company service. */
    private final CompanyService companyService;

    /**
     * Lists companies (paginated).
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the sort property
     * @param direction the sort direction
     * @return a page of company responses
     */
    @GetMapping
    public ResponseEntity<PageResponse<CompanyResponse>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PaginationUtils.toPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(companyService.getCompanies(pageable));
    }

    /**
     * Returns a single company.
     *
     * @param id the company id
     * @return the company response
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    /**
     * Creates a new company.
     *
     * @param request the company request
     * @return the created company response
     */
    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request));
    }

    /**
     * Updates an existing company.
     *
     * @param id the company id
     * @param request the updated fields
     * @return the updated company response
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    /**
     * Deletes a company.
     *
     * @param id the company id
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}