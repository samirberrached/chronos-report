export interface ReportLine {
  firstName: string;
  lastName: string;
  identifier: string;
  registrationNumber: number | null;
  company: string;
  period: number;
  organizationalUnit: string | null;
  product: string | null;
  activityNature: string | null;
  accountingCode: string | null;
  ratio: number;
}

export interface AnomalyLine {
  firstName: string;
  lastName: string;
  identifier: string;
  registrationNumber: number | null;
  company: string;
  period: number;
  organizationalUnit: string;
  product: string;
  activityNature: string;
}

export interface ReportGenerationSummary {
  totalEmployees: number;
  totalReportLines: number;
  totalAnomalies: number;
  reportFile: string;
  anomaliesFile: string;
  reportLines: ReportLine[];
  anomalyLines: AnomalyLine[];
}

export type UserProfile = "admin" | "financial" | "product";
