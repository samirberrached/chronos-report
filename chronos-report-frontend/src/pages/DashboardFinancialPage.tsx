import { useMemo } from "react";
import { useReport } from "../context/ReportContext";
import { KpiCard } from "../components/KpiCard";
import { DataTable } from "../components/DataTable";
import { BarChartCard } from "../components/charts/BarChartCard";
import { StackedBarChartCard, type StackedBarDatum } from "../components/charts/StackedBarChartCard";
import {
  ACCOUNTING_BUCKET_ALLOCATED,
  ACCOUNTING_BUCKET_LEAVE,
  ACCOUNTING_BUCKET_NO_TS,
  bucketAccountingCode,
  crossTabSum,
  groupSum,
  normalizeRowsToPercent,
  shareOfTotal,
} from "../utils/aggregate";

const BUCKET_SERIES = [ACCOUNTING_BUCKET_ALLOCATED, ACCOUNTING_BUCKET_NO_TS, ACCOUNTING_BUCKET_LEAVE];
const PCT = (v: number) => `${v}%`;

export function DashboardFinancialPage() {
  const { summary, loading } = useReport();

  const stats = useMemo(() => {
    if (!summary) return null;
    const lines = summary.reportLines;

    const totalRatio = lines.reduce((sum, l) => sum + l.ratio, 0);
    const nonProductiveRatio = lines
      .filter((l) => bucketAccountingCode(l.accountingCode) !== ACCOUNTING_BUCKET_ALLOCATED)
      .reduce((sum, l) => sum + l.ratio, 0);
    const pctNonProductive = totalRatio > 0 ? (nonProductiveRatio / totalRatio) * 100 : 0;

    const avgRatioPerLine = lines.length > 0 ? totalRatio / lines.length : 0;

    const employeesOnLeave = new Set(
      lines.filter((l) => bucketAccountingCode(l.accountingCode) === ACCOUNTING_BUCKET_LEAVE).map((l) => l.identifier)
    ).size;

    // Part de chaque entité dans le total alloué (%), pas somme brute : borné 0-100, comparable.
    // Trier AVANT l'arrondi à 1 décimale de shareOfTotal : deux entreprises très proches
    // (ex. 1.24% et 1.23%) arrondissent toutes les deux à "1.2%" — trier après coup produirait
    // un classement arbitraire entre elles au lieu du vrai classement par valeur réelle.
    const byCompany = shareOfTotal(
      groupSum(lines, (l) => l.company, (l) => l.ratio).sort((a, b) => b.value - a.value)
    );
    const byOrgUnit = shareOfTotal(
      groupSum(lines, (l) => l.organizationalUnit, (l) => l.ratio).sort((a, b) => b.value - a.value)
    );
    const topCompany = byCompany[0] ?? null;

    // Composition par type d'allocation, normalisée à 100% par entreprise. Pas par unité
    // organisationnelle : côté backend, les lignes "sans timesheet" remontent toujours à
    // l'unité PARENTE (ex. "MEA") tandis que les lignes avec un vrai timesheet utilisent
    // l'unité ENFANT précise (ex. "MEA MENA") — vérifié sur les 42 unités, aucune n'a de
    // composition mixte, chacune est 100% d'un seul type. Un stacked bar par unité ne montre
    // donc jamais de vraie composition. Par entreprise en revanche, les 108 mixent bien les
    // deux types : c'est la dimension pertinente pour ce graphique.
    const companyByBucket = normalizeRowsToPercent(
      crossTabSum(lines, (l) => l.company, (l) => bucketAccountingCode(l.accountingCode), (l) => l.ratio)
    );
    const companyByBucketChart: StackedBarDatum[] = companyByBucket.map(({ row, values }) => ({
      name: row,
      ...values,
    }));

    // Concentration du coût par entreprise : part de sa PLUS GROSSE unité organisationnelle
    // dans SON PROPRE total (pas le total global). Remplace le "top combinaisons Entreprise ×
    // Unité" par part du total global : avec ~3900 combinaisons et des entreprises de taille
    // très homogène, même la plus grosse combinaison ne pesait que 0,28 % du total — toutes
    // quasi identiques, sans intérêt. Normalisée par entreprise, cette mesure varie réellement
    // (15 % à 29 % observé) et répond à une vraie question : quelles entreprises dépendent
    // fortement d'une seule unité (risque de concentration) plutôt que d'être réparties.
    const byCompanyOrgUnitRaw = new Map<string, Map<string, number>>();
    for (const l of lines) {
      const orgUnit = l.organizationalUnit ?? "Non renseigné";
      if (!byCompanyOrgUnitRaw.has(l.company)) byCompanyOrgUnitRaw.set(l.company, new Map());
      const m = byCompanyOrgUnitRaw.get(l.company)!;
      m.set(orgUnit, (m.get(orgUnit) ?? 0) + l.ratio);
    }
    const companyConcentrationData = [...byCompanyOrgUnitRaw.entries()]
      .map(([company, orgMap]) => {
        const companyTotal = [...orgMap.values()].reduce((sum, v) => sum + v, 0);
        const topOrgUnitTotal = Math.max(...orgMap.values());
        return { name: company, value: companyTotal > 0 ? Math.round((topOrgUnitTotal / companyTotal) * 1000) / 10 : 0 };
      })
      .sort((a, b) => b.value - a.value);

    return {
      pctNonProductive,
      avgRatioPerLine,
      employeesOnLeave,
      byCompany,
      byOrgUnit,
      topCompany,
      companyByBucket,
      companyByBucketChart,
      companyConcentrationData,
    };
  }, [summary]);

  if (!summary || !stats) {
    return <p>Aucune donnée — cliquez sur « Générer le rapport ».</p>;
  }

  return (
    <div style={{ opacity: loading ? 0.5 : 1, transition: "opacity 150ms" }}>
      <h1>Financial Officer</h1>

      <div className="kpi-grid">
        <KpiCard label="Temps non productif" value={`${stats.pctNonProductive.toFixed(1)}%`} />
        <KpiCard label="Employés en congé complet" value={stats.employeesOnLeave} />
        <KpiCard
          label="Entreprise avec la plus grosse part de coût"
          value={stats.topCompany ? `${stats.topCompany.name} (${stats.topCompany.value}%)` : "—"}
        />
        <KpiCard label="Ratio moyen d'allocation par ligne" value={`${stats.avgRatioPerLine.toFixed(1)}%`} />
      </div>

      <div className="panel-grid">
        <StackedBarChartCard
          title="Composition du coût par entreprise, par type d'allocation (%)"
          data={stats.companyByBucketChart}
          series={BUCKET_SERIES}
          valueFormatter={PCT}
        />
        <BarChartCard
          title="Concentration du coût par entreprise (part de sa 1ère unité, %)"
          data={stats.companyConcentrationData}
          valueFormatter={PCT}
        />
        <BarChartCard title="Part du coût par entreprise (%)" data={stats.byCompany} valueFormatter={PCT} />
        <BarChartCard title="Part du coût par unité organisationnelle (%)" data={stats.byOrgUnit} valueFormatter={PCT} />
        <div className="panel">
          <h2>Composition par entreprise × type d'allocation (%)</h2>
          <DataTable
            rows={stats.companyByBucket}
            pageSize={50}
            columns={[
              { header: "Entreprise", accessor: (r) => r.row },
              { header: ACCOUNTING_BUCKET_ALLOCATED, accessor: (r) => `${r.values[ACCOUNTING_BUCKET_ALLOCATED] ?? 0}%` },
              { header: ACCOUNTING_BUCKET_NO_TS, accessor: (r) => `${r.values[ACCOUNTING_BUCKET_NO_TS] ?? 0}%` },
              { header: ACCOUNTING_BUCKET_LEAVE, accessor: (r) => `${r.values[ACCOUNTING_BUCKET_LEAVE] ?? 0}%` },
            ]}
          />
        </div>
      </div>
    </div>
  );
}
