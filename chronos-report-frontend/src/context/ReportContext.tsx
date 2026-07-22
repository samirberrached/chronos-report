import { createContext, useContext, useState, type ReactNode } from "react";
import { generateReport } from "../api/reportsApi";
import type { ReportGenerationSummary } from "../types/report";

interface ReportContextValue {
  monthPeriod: string;
  setMonthPeriod: (value: string) => void;
  summary: ReportGenerationSummary | null;
  loading: boolean;
  error: string | null;
  lastGeneratedAt: Date | null;
  refresh: () => Promise<void>;
}

const ReportContext = createContext<ReportContextValue | undefined>(undefined);

const DEFAULT_MONTH_PERIOD = "1|24";

export function ReportProvider({ children }: { children: ReactNode }) {
  const [monthPeriod, setMonthPeriod] = useState(DEFAULT_MONTH_PERIOD);
  const [summary, setSummary] = useState<ReportGenerationSummary | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [lastGeneratedAt, setLastGeneratedAt] = useState<Date | null>(null);

  async function refresh() {
    setLoading(true);
    setError(null);
    try {
      const result = await generateReport(monthPeriod);
      setSummary(result);
      setLastGeneratedAt(new Date());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Erreur inconnue");
    } finally {
      setLoading(false);
    }
  }

  return (
    <ReportContext.Provider
      value={{ monthPeriod, setMonthPeriod, summary, loading, error, lastGeneratedAt, refresh }}
    >
      {children}
    </ReportContext.Provider>
  );
}

export function useReport(): ReportContextValue {
  const ctx = useContext(ReportContext);
  if (!ctx) throw new Error("useReport doit être utilisé à l'intérieur d'un ReportProvider");
  return ctx;
}
