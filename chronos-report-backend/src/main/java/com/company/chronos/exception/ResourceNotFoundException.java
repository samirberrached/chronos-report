package com.company.chronos.exception;

/**
 * Thrown when a requested resource (entity, row, configuration) cannot be
 * located. Maps to HTTP 404 NOT FOUND.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a not-found exception for a named resource and identifier.
     *
     * @param resourceName the resource type (e.g. "Employee")
     * @param field the field used to look it up (e.g. "id")
     * @param value the value that was not found
     */
    public ResourceNotFoundException(String resourceName, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resourceName, field, value));
    }

    /**
     * Constructs a not-found exception with a custom message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}