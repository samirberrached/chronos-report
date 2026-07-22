import { useMemo } from "react";
import { useReport } from "../context/ReportContext";
import { KpiCard } from "../components/KpiCard";
import { AnomalyList } from "../components/AnomalyList";
import { BarChartCard } from "../components/charts/BarChartCard";
import { PieChartCard } from "../components/charts/PieChartCard";
import { NO_TS_PREFIX, anomalyTypeBreakdown, groupCount } from "../utils/aggregate";

export function DashboardAdminPage() {
  const { summary, loading } = useReport();

  const stats = useMemo(() => {
    if (!summary) return null;
    const { reportLines, anomalyLines } = summary;

    const processedIdentifiers = new Set([
      ...reportLines.map((l) => l.identifier),
      ...anomalyLines.map((a) => a.identifier),
    ]);
    const totalEmployeesProcessed = processedIdentifiers.size;

    const totalLines = reportLines.length + anomalyLines.length;
    const anomalyRate = totalLines > 0 ? (anomalyLines.length / totalLines) * 100 : 0;

    const employeesWithoutTimesheet = new Set(
      reportLines.filter((l) => l.accountingCode?.startsWith(NO_TS_PREFIX)).map((l) => l.identifier)
    ).size;

    const missingByType = anomalyTypeBreakdown(anomalyLines);
    const anomaliesByCompany = groupCount(anomalyLines, (a) => a.company);

    return { totalEmployeesProcessed, anomalyRate, employeesWithoutTimesheet, missingByType, anomaliesByCompany };
  }, [summary]);

  if (!summary || !stats) {
    return <p>Aucune donnée — cliquez sur « Générer le rapport ».</p>;
  }

  return (
    <div style={{ opacity: loading ? 0.5 : 1, transition: "opacity 150ms" }}>
      <h1>Data Administrator</h1>

      <div className="kpi-grid">
        <KpiCard label="Employés traités" value={stats.totalEmployeesProcessed} />
        <KpiCard label="Anomalies détectées" value={summary.totalAnomalies} />
        <KpiCard label="Taux d'anomalie global" value={`${stats.anomalyRate.toFixed(1)}%`} />
        <KpiCard label="Employés sans timesheet" value={stats.employeesWithoutTimesheet} />
      </div>

      <div className="panel-grid">
        <BarChartCard title="Anomalies par entreprise" data={stats.anomaliesByCompany} />
        <PieChartCard title="Répartition des anomalies par type manquant" data={stats.missingByType} />
        <div className="panel">
          <h2>Détail des anomalies</h2>
          <AnomalyList anomalies={summary.anomalyLines} />
        </div>
      </div>
    </div>
  );
}
