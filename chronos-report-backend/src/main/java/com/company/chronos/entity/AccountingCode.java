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
 * Dimension entity representing a general-ledger accounting code used to book
 * allocated employee cost.
 *
 * <p>The natural/business key is the {@code code} which is unique per
 * company.</p>
 */
@Entity
@Table(
        name = "accounting_code",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_accounting_code_company", columnNames = {"code", "company_id"})
        },
        indexes = {
                @Index(name = "idx_accounting_code_company", columnList = "company_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountingCode extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: the GL account code. */
    @Column(name = "code", nullable = false, length = 30)
    private String code;

    /** Human-readable description of the accounting code. */
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    /** Owning company. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}