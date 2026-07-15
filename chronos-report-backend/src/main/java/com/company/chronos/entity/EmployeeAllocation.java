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
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fact entity representing the cost allocated from an {@link Employee} to a
 * {@link Product} / {@link Activity} for a given reporting month.
 *
 * <p>Allocations are derived from {@link EmployeeTime} entries and the
 * employee's monthly cost, then booked against an {@link AccountingCode}.</p>
 */
@Entity
@Table(
        name = "employee_allocation",
        schema = "reporting",
        indexes = {
                @Index(name = "idx_empalloc_employee", columnList = "employee_id"),
                @Index(name = "idx_empalloc_product", columnList = "product_id"),
                @Index(name = "idx_empalloc_accounting", columnList = "accounting_code_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAllocation extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Employee the cost is allocated from. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /** Product the cost is allocated to. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Activity the cost is allocated to. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    /** Accounting code the cost is booked against. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id", nullable = false)
    private AccountingCode accountingCode;

    /** Reporting month (yyyy-MM) this allocation belongs to. */
    @Column(name = "month", nullable = false, length = 7)
    private String month;

    /** Allocated cost amount in the company currency. */
    @Column(name = "allocated_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal allocatedCost;

    /** Allocation percentage (0-100) of the employee's monthly cost. */
    @Column(name = "allocation_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal allocationPercentage;
}