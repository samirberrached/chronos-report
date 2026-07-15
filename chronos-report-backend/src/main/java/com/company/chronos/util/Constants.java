package com.company.chronos.util;

/**
 * Centralised application-wide constants. Keeping magic strings and numbers in
 * one place avoids drift between layers (e.g. security, validation, export).
 */
public final class Constants {

    /** Private constructor to prevent instantiation of a constants holder. */
    private Constants() {
    }

    /** API base path prefix applied via context-path in application.yml. */
    public static final String API_BASE_PATH = "/api";

    /** Public authentication endpoints (no JWT required). */
    public static final String AUTH_BASE = "/auth";

    /** JWT cookie/header name. */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** JWT bearer prefix. */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Application role names. */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_FINANCE_ANALYST = "FINANCE_ANALYST";
    public static final String ROLE_VIEWER = "VIEWER";

    /** Default page size for paginated endpoints. */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /** Maximum allowed page size. */
    public static final int MAX_PAGE_SIZE = 100;

    /** Reporting month format (yyyy-MM). */
    public static final String MONTH_FORMAT = "yyyy-MM";

    /** CSV export content type. */
    public static final String CONTENT_TYPE_CSV = "text/csv";

    /** Excel export content type. */
    public static final String CONTENT_TYPE_EXCEL =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
}