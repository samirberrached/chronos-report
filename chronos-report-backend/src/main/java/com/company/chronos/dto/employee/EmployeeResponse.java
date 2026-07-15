package com.company.chronos.dto.employee;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing an {@code Employee}, with denormalized
 * company and organization names for convenience.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Business key: employee code within the company. */
    private String employeeCode;

    /** Full display name. */
    private String fullName;

    /** Email address. */
    private String email;

    /** Monthly gross cost. */
    private BigDecimal monthlyCost;

    /** Standard contracted hours per month. */
    private Integer standardMonthlyHours;

    /** Owning company id. */
    private Long companyId;

    /** Owning company name. */
    private String companyName;

    /** Organization unit id. */
    private Long organizationId;

    /** Organization unit name. */
    private String organizationName;

    /** Audit: creation instant. */
    private Instant createdAt;

    /** Audit: last modification instant. */
    private Instant lastModifiedAt;
}