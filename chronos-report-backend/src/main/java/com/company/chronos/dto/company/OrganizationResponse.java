package com.company.chronos.dto.company;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response payload representing an {@code Organization} unit, including its
 * parent and owning company identifiers.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationResponse {

    /** Surrogate primary key. */
    private Long id;

    /** Business key: organization code within the company. */
    private String organizationCode;

    /** Display name. */
    private String name;

    /** Owning company id. */
    private Long companyId;

    /** Owning company name. */
    private String companyName;

    /** Parent organization id (null for a root node). */
    private Long parentId;

    /** Parent organization name (null for a root node). */
    private String parentName;

    /** Audit: creation instant. */
    private Instant createdAt;

    /** Audit: last modification instant. */
    private Instant lastModifiedAt;
}