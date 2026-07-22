import { useEffect, useState } from "react";
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useReport } from "../context/ReportContext";
import { getReportDownloadUrl } from "../api/reportsApi";
import { ReportTable } from "../components/ReportTable";
import { AnomalyList } from "../components/AnomalyList";
import vermegLogo from "../assets/vermeg-logo.svg";

const MONTH_OPTIONS = Array.from({ length: 12 }, (_, i) => {
  const month = i + 1;
  return { value: `${month}|24`, label: `${String(month).padStart(2, "0")}/2024` };
});

const PROFILE_LABELS: Record<string, string> = {
  admin: "Data Administrator",
  financial: "Financial Officer",
  product: "Product Manager",
};

export function AppLayout() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();
  const { monthPeriod, setMonthPeriod, summary, loading, error, lastGeneratedAt, refresh } = useReport();
  const [showReportTable, setShowReportTable] = useState(false);
  const [showAnomalyTable, setShowAnomalyTable] = useState(false);

  useEffect(() => {
    if (auth) {
      refresh();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [auth, monthPeriod]);

  if (!auth) {
    return <Navigate to="/login" replace />;
  }

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="app-shell">
      <div className="top-bar">
        <div className="brand">
          <img src={vermegLogo} alt="VERMEG" className="vermeg-logo" />
          <span className="brand-suffix">Chronos Report</span>
        </div>

        <select
          className="select"
          value={monthPeriod}
          onChange={(e) => setMonthPeriod(e.target.value)}
        >
          {MONTH_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>

        <button className="btn btn-secondary" onClick={() => refresh()} disabled={loading}>
          {loading ? "Génération…" : "Générer le rapport"}
        </button>

        <button
          className="btn btn-secondary"
          onClick={() => setShowReportTable((v) => !v)}
          disabled={!summary}
        >
          {showReportTable ? "Masquer le rapport" : "Voir le rapport"}
        </button>

        <a
          className="btn btn-secondary"
          href={summary ? getReportDownloadUrl(monthPeriod, "report") : undefined}
          aria-disabled={!summary}
          onClick={(e) => {
            if (!summary) e.preventDefault();
          }}
        >
          CSV Rapport
        </a>

        <button
          className="btn btn-secondary"
          onClick={() => setShowAnomalyTable((v) => !v)}
          disabled={!summary}
        >
          {showAnomalyTable ? "Masquer les anomalies" : "Voir les anomalies"}
        </button>

        <a
          className="btn btn-secondary"
          href={summary ? getReportDownloadUrl(monthPeriod, "anomalies") : undefined}
          aria-disabled={!summary}
          onClick={(e) => {
            if (!summary) e.preventDefault();
          }}
        >
          CSV Anomalies
        </a>

        {error && <span className="status error">{error}</span>}
        {!error && summary && (
          <span className="status">
            {summary.totalEmployees} employés · {summary.totalAnomalies} anomalies
            {lastGeneratedAt && (
              <span className="status-timestamp">
                {" "}
                · Rapport généré ✓ à {lastGeneratedAt.toLocaleTimeString("fr-FR")}
              </span>
            )}
          </span>
        )}

        <span className="profile-badge">{PROFILE_LABELS[auth.profile] ?? auth.profile}</span>
        <button className="btn btn-secondary" onClick={handleLogout}>
          Déconnexion
        </button>
      </div>

      {showReportTable && summary && (
        <div className="page-content" style={{ paddingBottom: 0 }}>
          <div className="panel">
            <h2>Analytic EmployeeTime Report — {summary.totalReportLines} lignes</h2>
            <ReportTable lines={summary.reportLines} />
          </div>
        </div>
      )}

      {showAnomalyTable && summary && (
        <div className="page-content" style={{ paddingBottom: 0 }}>
          <div className="panel">
            <h2>Analytic EmployeeTime Anomalies — {summary.totalAnomalies} lignes</h2>
            <AnomalyList anomalies={summary.anomalyLines} />
          </div>
        </div>
      )}

      <div className="page-content">
        <Outlet />
      </div>
    </div>
  );
}
