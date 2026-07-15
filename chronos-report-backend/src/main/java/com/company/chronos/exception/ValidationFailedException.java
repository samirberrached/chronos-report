package com.company.chronos.exception;

import java.util.List;

/**
 * Thrown when request validation fails at the service layer (beyond what Bean
 * Validation covers). Maps to HTTP 400 BAD REQUEST. Carries a list of
 * field-level error messages for structured reporting.
 */
public class ValidationFailedException extends RuntimeException {

    /** The list of validation error messages. */
    private final List<String> errors;

    /**
     * Constructs a validation failure with a single message.
     *
     * @param message the detail message
     */
    public ValidationFailedException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    /**
     * Constructs a validation failure with multiple field-level messages.
     *
     * @param message the summary message
     * @param errors the list of field-level error messages
     */
    public ValidationFailedException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Returns the list of field-level error messages.
     *
     * @return the error messages
     */
    public List<String> getErrors() {
        return errors;
    }
}