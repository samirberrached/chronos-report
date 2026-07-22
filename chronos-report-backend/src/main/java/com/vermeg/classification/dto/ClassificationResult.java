package com.vermeg.classification.dto;

import java.util.ArrayList;
import java.util.List;

public record ClassificationResult(List<ReportLine> reportLines, List<AnomalyLine> anomalyLines) {

    public static ClassificationResult report(ReportLine... lines) {
        return new ClassificationResult(List.of(lines), List.of());
    }

    public static ClassificationResult anomaly(AnomalyLine line) {
        return new ClassificationResult(List.of(), List.of(line));
    }

    public ClassificationResult merge(ClassificationResult other) {
        List<ReportLine> mergedReport = new ArrayList<>(this.reportLines);
        mergedReport.addAll(other.reportLines);
        List<AnomalyLine> mergedAnomalies = new ArrayList<>(this.anomalyLines);
        mergedAnomalies.addAll(other.anomalyLines);
        return new ClassificationResult(mergedReport, mergedAnomalies);
    }
}
