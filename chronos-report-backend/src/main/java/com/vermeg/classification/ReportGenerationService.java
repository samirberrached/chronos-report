package com.vermeg.classification;

import com.vermeg.classification.dto.AnomalyLine;
import com.vermeg.classification.dto.ClassificationResult;
import com.vermeg.classification.dto.ReportLine;
import com.vermeg.entity.companymember.CompanyMember;
import com.vermeg.entity.companymember.CompanyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportGenerationService {

    private static final String[] REPORT_HEADER = {
            "FirstName", "LastName", "Identifier", "RegistrationNumber", "Company",
            "period", "OrganizationalUnit", "product", "ActivityNature", "AccountingCode", "Ratio"
    };

    private static final String[] ANOMALIES_HEADER = {
            "FirstName", "LastName", "Identifier", "RegistrationNumber", "Company",
            "period", "OrganizationalUnit", "product", "ActivityNature", "AccountingCode", "Ratio"
    };

    @Autowired private CompanyMemberRepository companyMemberRepository;
    @Autowired private ClassificationService classificationService;
    @Autowired private ClassificationContextLoader classificationContextLoader;

    @Value("${chronos.classification.output-dir:./reports}")
    private String outputDir;

    public ReportGenerationSummary generateReport(String monthPeriodStr) throws IOException {
        YearMonth yearMonth = parseYearMonth(monthPeriodStr);

        List<CompanyMember> members = companyMemberRepository.findActiveForPeriod(yearMonth.atDay(1), yearMonth.atEndOfMonth());
        ClassificationContext context = classificationContextLoader.load(yearMonth.atDay(1), yearMonth.atEndOfMonth());

        List<ReportLine> reportLines = new ArrayList<>();
        List<AnomalyLine> anomalyLines = new ArrayList<>();

        for (CompanyMember member : members) {
            ClassificationResult result = classificationService.classify(member, monthPeriodStr, context);
            reportLines.addAll(result.reportLines());
            anomalyLines.addAll(result.anomalyLines());
        }

        Files.createDirectories(Path.of(outputDir));

        Path reportFile = resolveOutputFile(yearMonth, "report");
        Path anomaliesFile = resolveOutputFile(yearMonth, "anomalies");

        writeReportCsv(reportLines, reportFile);
        writeAnomaliesCsv(anomalyLines, anomaliesFile);

        return new ReportGenerationSummary(members.size(), reportLines.size(), anomalyLines.size(),
                reportFile, anomaliesFile, reportLines, anomalyLines);
    }

    public Path resolveOutputFile(String monthPeriodStr, String type) {
        return resolveOutputFile(parseYearMonth(monthPeriodStr), type);
    }

    private Path resolveOutputFile(YearMonth yearMonth, String type) {
        String label = "%02d_%04d".formatted(yearMonth.getMonthValue(), yearMonth.getYear());
        String prefix = "anomalies".equalsIgnoreCase(type)
                ? "Analytic_EmployeeTime_Anomalies_"
                : "Analytic_EmployeeTime_Report_";
        return Path.of(outputDir, prefix + label + ".csv");
    }

    private YearMonth parseYearMonth(String monthPeriodStr) {
        String[] parts = monthPeriodStr.split("\\|");
        return YearMonth.of(Integer.parseInt("20" + parts[1]), Integer.parseInt(parts[0]));
    }

    private void writeReportCsv(List<ReportLine> lines, Path file) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeRow(writer, REPORT_HEADER);
            for (ReportLine line : lines) {
                writeRow(writer,
                        line.firstName(), line.lastName(), line.identifier(),
                        line.registrationNumber() != null ? String.valueOf(line.registrationNumber()) : "",
                        line.company(), String.valueOf(line.period()),
                        line.organizationalUnit(), line.product(), line.activityNature(),
                        line.accountingCode(), String.valueOf(line.ratio()));
            }
        }
    }

    private void writeAnomaliesCsv(List<AnomalyLine> lines, Path file) throws IOException {
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writeRow(writer, ANOMALIES_HEADER);
            for (AnomalyLine line : lines) {
                writeRow(writer,
                        line.firstName(), line.lastName(), line.identifier(),
                        line.registrationNumber() != null ? String.valueOf(line.registrationNumber()) : "",
                        line.company(), String.valueOf(line.period()),
                        line.organizationalUnit(), line.product(), line.activityNature(),
                        "", "");
            }
        }
    }

    private void writeRow(Writer writer, String... values) throws IOException {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) row.append(',');
            row.append(csvEscape(values[i]));
        }
        row.append('\n');
        writer.write(row.toString());
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public record ReportGenerationSummary(int totalEmployees, int totalReportLines, int totalAnomalies,
                                           Path reportFile, Path anomaliesFile,
                                           List<ReportLine> reportLines, List<AnomalyLine> anomalyLines) {}
}
