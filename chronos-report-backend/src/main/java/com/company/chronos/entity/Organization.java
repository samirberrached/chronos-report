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
 * Dimension entity representing an organizational unit (department, division,
 * cost center) within a {@link Company}.
 *
 * <p>Organizations form a self-referencing hierarchy via {@code parent}. The
 * natural/business key is the {@code organizationCode} which is unique per
 * company.</p>
 */
@Entity
@Table(
        name = "organization",
        schema = "reporting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_org_code_company", columnNames = {"organization_code", "company_id"})
        },
        indexes = {
                @Index(name = "idx_org_company", columnList = "company_id"),
                @Index(name = "idx_org_parent", columnList = "parent_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends Auditable {

    /** Surrogate primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Business key: short code identifying the organization within a company. */
    @Column(name = "organization_code", nullable = false, length = 30)
    private String organizationCode;

    /** Display name of the organization. */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /** Owning company. */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    /** Parent organization in the hierarchy (nullable for root nodes). */
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Organization parent;
}