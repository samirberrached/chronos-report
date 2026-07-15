package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dimension entity representing an employee who records time and receives cost
 * allocations.
 *
 * <p>Employees belong to a {@link Company} and an {@link Organization}. The
 * natural/business key is the {@code employeeCode} which is unique per
 * company.</p>
 */
@Entity
@Table(
        name = "employee",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employee_code_company", columnNames = {"employee_code", "company_id"})
        },
        indexes = {
                @Index(name = "idx_employee_company", columnList = "company_id"),
                @Index(name = "idx_employee_organization", columnList = "organization_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: the employee identifier within their company. */
    @Column(name = "employee_code", nullable = false, length = 50)
    private String employeeCode;

    /** Full display name of the employee. */
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    /** Email address, used for authentication and notifications. */
    @Column(name = "email", length = 255)
    private String email;

    /** Monthly gross cost (salary + charges) in the company currency. */
    @Column(name = "monthly_cost", nullable = false)
    private java.math.BigDecimal monthlyCost;

    /** Standard contracted hours per month, used for allocation ratios. */
    @Column(name = "standard_monthly_hours", nullable = false)
    private Integer standardMonthlyHours;

    /** Owning company. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /** Organization unit the employee belongs to. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}