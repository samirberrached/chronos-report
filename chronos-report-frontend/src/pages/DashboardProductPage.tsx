import { useMemo } from "react";
import { useReport } from "../context/ReportContext";
import { KpiCard } from "../components/KpiCard";
import { DataTable } from "../components/DataTable";
import { BarChartCard } from "../components/charts/BarChartCard";
import { PieChartCard } from "../components/charts/PieChartCard";
import { NOT_FOUND, groupAverage, groupCountDistinct, groupSum, shareOfTotal } from "../utils/aggregate";

export function DashboardProductPage() {
  const { summary, loading } = useReport();

  const stats = useMemo(() => {
    if (!summary) return null;
    const lines = summary.reportLines;

    const employeesPerProduct = groupCountDistinct(lines, (l) => l.product, (l) => l.identifier);
    // Trié par valeur brute AVANT la conversion en part (%) : évite qu'un arrondi à 1 décimale
    // fasse paraître deux produits proches "à égalité" et inverse leur ordre réel.
    const timeByProductRaw = groupSum(lines, (l) => l.product, (l) => l.ratio).sort((a, b) => b.value - a.value);
    const timeByProduct = shareOfTotal(timeByProductRaw);
    const averageRatioPerProduct = groupAverage(lines, (l) => l.product, (l) => l.ratio);

    const totalEmployeesInvolved = new Set(lines.map((l) => l.identifier)).size;

    const topProduct = timeByProductRaw[0] ?? null;

    const perEmployeeProduct = new Map<string, Map<string, number>>();
    for (const l of lines) {
      if (!l.product) continue;
      if (!perEmployeeProduct.has(l.identifier)) perEmployeeProduct.set(l.identifier, new Map());
      const products = perEmployeeProduct.get(l.identifier)!;
      products.set(l.product, (products.get(l.product) ?? 0) + l.ratio);
    }
    const multiAssigned = [...perEmployeeProduct.entries()]
      .filter(([, products]) => products.size > 1)
      .map(([identifier, products]) => ({
        identifier,
        products: [...products.keys()].join(", "),
        ratioDetail: [...products.entries()]
          .map(([product, ratio]) => `${product}: ${Math.round(ratio * 100) / 100}%`)
          .join(", "),
      }));

    const productsWithMissingData = summary.anomalyLines.filter((a) => a.product === NOT_FOUND).length;

    return {
      employeesPerProduct,
      timeByProduct,
      averageRatioPerProduct,
      totalEmployeesInvolved,
      topProduct,
      multiAssigned,
      productsWithMissingData,
    };
  }, [summary]);

  if (!summary || !stats) {
    return <p>Aucune donnée — cliquez sur « Générer le rapport ».</p>;
  }

  return (
    <div style={{ opacity: loading ? 0.5 : 1, transition: "opacity 150ms" }}>
      <h1>Product Manager</h1>

      <div className="kpi-grid">
        <KpiCard label="Produits actifs" value={stats.employeesPerProduct.length} />
        <KpiCard label="Employés impliqués" value={stats.totalEmployeesInvolved} />
        <KpiCard label="Produit avec le plus d'allocation" value={stats.topProduct?.name ?? "—"} />
        <KpiCard label="Anomalies produit (NOT FOUND)" value={stats.productsWithMissingData} />
      </div>

      <div className="panel-grid">
        <PieChartCard
          title="Répartition du temps total par produit"
          data={stats.timeByProduct}
          valueFormatter={(v) => `${v}%`}
        />
        <BarChartCard title="Employés par produit" data={stats.employeesPerProduct} />
        <BarChartCard
          title="Ratio moyen d'allocation par produit"
          data={stats.averageRatioPerProduct}
          valueFormatter={(v) => `${v}%`}
        />
        <div className="panel">
          <h2>Employés multi-produits (&gt;1 produit)</h2>
          <DataTable
            rows={stats.multiAssigned}
            emptyMessage="Aucun employé multi-affecté ce mois-ci."
            columns={[
              { header: "Identifiant", accessor: (r) => r.identifier },
              { header: "Produits", accessor: (r) => r.products },
              { header: "Ratio par produit", accessor: (r) => r.ratioDetail },
            ]}
          />
        </div>
      </div>
    </div>
  );
}
