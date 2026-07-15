package com.company.chronos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Dimension entity representing a legal company (client or internal entity)
 * for which cost allocation is performed.
 *
 * <p>The natural/business key is the {@code companyCode}, which is globally
 * unique across the reporting schema.</p>
 */
@Entity
@Table(
        name = "company",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_company_code", columnNames = "company_code")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: short unique code identifying the company. */
    @Column(name = "company_code", nullable = false, length = 30)
    private String companyCode;

    /** Legal name of the company. */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** ISO-4217 currency code used for monetary amounts of this company. */
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /** Whether the company is active and eligible for reporting. */
    @Column(name = "active", nullable = false)
    private boolean active;
}