package com.company.chronos.service;

import com.company.chronos.dto.report.ExportRequest;

/**
 * Service contract for exporting report allocations in a requested format
 * (CSV or Excel).
 */
public interface ExportService {

    /**
     * Exports the allocations for a company and month in the requested format
     * and returns the raw bytes plus the content type.
     *
     * @param request the export request
     * @return the export result (bytes, content type, filename)
     */
    ExportResult exportReport(ExportRequest request);

    /**
     * Value object bundling an export's bytes, content type and filename.
     *
     * @param bytes the serialized content
     * @param contentType the HTTP content type
     * @param filename the suggested download filename
     */
    record ExportResult(byte[] bytes, String contentType, String filename) {
    }
}