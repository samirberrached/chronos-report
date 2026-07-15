package com.company.chronos.dto.anomaly;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for acknowledging/resolving an {@code EmployeeAnomaly}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveAnomalyRequest {

    /** Whether to mark the anomaly as resolved. */
    @NotNull(message = "Resolved flag is required")
    private Boolean resolved;
}