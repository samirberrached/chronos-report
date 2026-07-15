package com.company.chronos.exception;

/**
 * Thrown when an attempt is made to create a resource whose natural/business
 * key already exists (e.g. a duplicate employee code within a company). Maps
 * to HTTP 409 CONFLICT.
 */
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a duplicate-resource exception for a named resource and key.
     *
     * @param resourceName the resource type (e.g. "Employee")
     * @param field the business key field (e.g. "employeeCode")
     * @param value the conflicting value
     */
    public DuplicateResourceException(String resourceName, String field, Object value) {
        super(String.format("%s already exists with %s: '%s'", resourceName, field, value));
    }

    /**
     * Constructs a duplicate-resource exception with a custom message.
     *
     * @param message the detail message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}