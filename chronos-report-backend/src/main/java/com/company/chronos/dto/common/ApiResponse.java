package com.company.chronos.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic envelope used for successful single-object API responses.
 *
 * @param <T> the payload type
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** Whether the operation succeeded. */
    private boolean success;

    /** Human-readable message describing the outcome. */
    private String message;

    /** The response payload. */
    private T data;
}