package com.company.chronos.exception;

/**
 * Thrown when an operation would violate a domain/business rule (e.g. an
 * allocation percentage exceeding 100, or generating a report for a closed
 * month). Maps to HTTP 422 UNPROCESSABLE ENTITY.
 */
public class BusinessRuleViolationException extends RuntimeException {

    /**
     * Constructs a business-rule violation with a custom message.
     *
     * @param message the detail message describing the violated rule
     */
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}