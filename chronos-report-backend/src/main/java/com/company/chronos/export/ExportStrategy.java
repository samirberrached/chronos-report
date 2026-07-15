package com.company.chronos.export;

import com.company.chronos.dto.allocation.AllocationResponse;
import java.util.List;

/**
 * Strategy abstraction for exporting allocation data in a particular format.
 *
 * <p>Implementations (CSV, Excel) are selected at runtime by the
 * {@link ExportService} based on the requested format.</p>
 */
public interface ExportStrategy {

    /**
     * Returns the format identifier this strategy handles (e.g. "csv").
     *
     * @return the format identifier
     */
    String getFormat();

    /**
     * Exports the given allocations to a byte array in this strategy's format.
     *
     * @param allocations the allocation rows to export
     * @param month the reporting month (used in the file/title)
     * @return the serialized export content
     */
    byte[] export(List<AllocationResponse> allocations, String month);
}