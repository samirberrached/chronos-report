package com.company.chronos.controller;

import com.company.chronos.dto.common.PageResponse;
import com.company.chronos.dto.employee.EmployeeRequest;
import com.company.chronos.dto.employee.EmployeeResponse;
import com.company.chronos.service.EmployeeService;
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
 * REST controller for {@code Employee} dimension management.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    /** Employee service. */
    private final EmployeeService employeeService;

    /**
     * Lists employees for a company (paginated).
     *
     * @param companyId the owning company id
     * @param page the page number (0-based)
     * @param size the page size
     * @param sortBy the sort property
     * @param direction the sort direction
     * @return a page of employee responses
     */
    @GetMapping
    public ResponseEntity<PageResponse<EmployeeResponse>> getEmployees(
            @RequestParam Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PaginationUtils.toPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(employeeService.getEmployees(companyId, pageable));
    }

    /**
     * Returns a single employee.
     *
     * @param id the employee id
     * @return the employee response
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    /**
     * Creates a new employee.
     *
     * @param request the employee request
     * @return the created employee response
     */
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.createEmployee(request));
    }

    /**
     * Updates an existing employee.
     *
     * @param id the employee id
     * @param request the updated fields
     * @return the updated employee response
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    /**
     * Deletes an employee.
     *
     * @param id the employee id
     * @return no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}