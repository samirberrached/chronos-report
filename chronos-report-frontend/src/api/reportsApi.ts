import type { ReportGenerationSummary } from "../types/report";

export const API_BASE_URL = "http://localhost:8080/api";

export async function generateReport(monthPeriod: string): Promise<ReportGenerationSummary> {
  const url = `${API_BASE_URL}/reports?monthPeriod=${encodeURIComponent(monthPeriod)}`;
  const response = await fetch(url);

  if (!response.ok) {
    const body = await response.text().catch(() => "");
    throw new Error(`Échec de la génération du rapport (${response.status}) : ${body}`);
  }

  return response.json();
}

export function getReportDownloadUrl(monthPeriod: string, type: "report" | "anomalies"): string {
  return `${API_BASE_URL}/reports/download?monthPeriod=${encodeURIComponent(monthPeriod)}&type=${type}`;
}
