package com.company.chronos.export;

import com.company.chronos.dto.allocation.AllocationResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CSV implementation of {@link ExportStrategy}. Produces a UTF-8 CSV document
 * with a header row and one row per allocation.
 */
@Slf4j
@Component
public class CsvExportService implements ExportStrategy {

    /** Format identifier handled by this strategy. */
    private static final String FORMAT = "csv";

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public byte[] export(List<AllocationResponse> allocations, String month) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {
            writer.println("month,employeeId,employeeName,productName,activityName,"
                    + "accountingCode,allocatedCost,allocationPercentage");
            for (AllocationResponse a : allocations) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        csv(a.getMonth()), csv(a.getEmployeeId()),
                        csv(a.getEmployeeName()), csv(a.getProductName()),
                        csv(a.getActivityName()), csv(a.getAccountingCode()),
                        csv(a.getAllocatedCost()), csv(a.getAllocationPercentage()));
            }
        } catch (IOException ex) {
            log.error("Failed to write CSV export for month {}", month, ex);
            throw new com.company.chronos.exception.BusinessRuleViolationException(
                    "Failed to generate CSV export");
        }
        return baos.toByteArray();
    }

    /**
     * Escapes a value for safe inclusion in a CSV field.
     *
     * @param value the value (may be null)
     * @return the CSV-safe representation
     */
    private String csv(Object value) {
        if (value == null) {
            return "";
        }
        String s = value.toString();
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
}