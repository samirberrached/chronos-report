package com.company.chronos.export;

import com.company.chronos.dto.allocation.AllocationResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Excel (.xlsx) implementation of {@link ExportStrategy} using Apache POI.
 * Produces a single-sheet workbook with a styled header row.
 */
@Slf4j
@Component
public class ExcelExportService implements ExportStrategy {

    /** Format identifier handled by this strategy. */
    private static final String FORMAT = "excel";

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public byte[] export(List<AllocationResponse> allocations, String month) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Allocations-" + month);

            Row header = sheet.createRow(0);
            String[] columns = {"Month", "Employee ID", "Employee Name", "Product",
                    "Activity", "Accounting Code", "Allocated Cost",
                    "Allocation %"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (AllocationResponse a : allocations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nullToEmpty(a.getMonth()));
                row.createCell(1).setCellValue(nullToEmpty(a.getEmployeeId()));
                row.createCell(2).setCellValue(nullToEmpty(a.getEmployeeName()));
                row.createCell(3).setCellValue(nullToEmpty(a.getProductName()));
                row.createCell(4).setCellValue(nullToEmpty(a.getActivityName()));
                row.createCell(5).setCellValue(nullToEmpty(a.getAccountingCode()));
                row.createCell(6).setCellValue(
                        a.getAllocatedCost() != null ? a.getAllocatedCost().doubleValue() : 0.0);
                row.createCell(7).setCellValue(
                        a.getAllocationPercentage() != null
                                ? a.getAllocationPercentage().doubleValue() : 0.0);
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            log.error("Failed to write Excel export for month {}", month, ex);
            throw new com.company.chronos.exception.BusinessRuleViolationException(
                    "Failed to generate Excel export");
        }
    }

    /**
     * Converts a possibly-null value to an empty string.
     *
     * @param value the value
     * @return the string representation or empty string
     */
    private String nullToEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}