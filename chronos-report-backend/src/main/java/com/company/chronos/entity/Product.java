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
 * Dimension entity representing a product, project or deliverable to which
 * employee time and cost can be allocated.
 *
 * <p>The natural/business key is the {@code productCode} which is unique per
 * company.</p>
 */
@Entity
@Table(
        name = "product",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_code_company", columnNames = {"product_code", "company_id"})
        },
        indexes = {
                @Index(name = "idx_product_company", columnList = "company_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: short code identifying the product within a company. */
    @Column(name = "product_code", nullable = false, length = 30)
    private String productCode;

    /** Display name of the product. */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Whether the product is billable to a customer. */
    @Column(name = "billable", nullable = false)
    private boolean billable;

    /** Owning company. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}