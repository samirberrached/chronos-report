import type { ReportLine } from "../types/report";
import { DataTable } from "./DataTable";

export function ReportTable({ lines }: { lines: ReportLine[] }) {
  return (
    <DataTable
      rows={lines}
      pageSize={50}
      emptyMessage="Aucune ligne de rapport."
      columns={[
        { header: "Employé", accessor: (l) => `${l.firstName} ${l.lastName}` },
        { header: "Identifiant", accessor: (l) => l.identifier },
        { header: "Matricule", accessor: (l) => l.registrationNumber ?? "" },
        { header: "Entreprise", accessor: (l) => l.company },
        { header: "Période", accessor: (l) => l.period },
        { header: "Unité org.", accessor: (l) => l.organizationalUnit ?? "" },
        { header: "Produit", accessor: (l) => l.product ?? "" },
        { header: "Nature activité", accessor: (l) => l.activityNature ?? "" },
        { header: "Code compta", accessor: (l) => l.accountingCode ?? "" },
        { header: "Ratio", accessor: (l) => `${l.ratio.toFixed(2)}%` },
      ]}
    />
  );
}
