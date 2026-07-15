package com.company.chronos.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Consistent JSON error shape returned by the {@link GlobalExceptionHandler}.
 *
 * <p>Serialized as {@code { timestamp, status, error, message, path }}, with an
 * optional {@code errors} list for field-level validation failures.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /** Instant at which the error occurred. */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    /** HTTP status code. */
    private int status;

    /** HTTP status reason phrase (e.g. "Not Found"). */
    private String error;

    /** Human-readable error message. */
    private String message;

    /** Request path that produced the error. */
    private String path;

    /** Optional list of field-level validation error messages. */
    private List<String> errors;
}