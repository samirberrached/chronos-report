package com.company.chronos.dto.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for creating or updating an {@code Organization} unit.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationRequest {

    /** Business key: organization code within the company. */
    @NotBlank(message = "Organization code is required")
    @Size(max = 30, message = "Organization code must be at most 30 characters")
    private String organizationCode;

    /** Display name. */
    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be at most 200 characters")
    private String name;

    /** Owning company id. */
    @NotNull(message = "Company id is required")
    private Long companyId;

    /** Parent organization id (null for a root node). */
    private Long parentId;
}