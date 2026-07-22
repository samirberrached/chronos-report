import type { AnomalyLine } from "../types/report";
import { DataTable } from "./DataTable";

export function AnomalyList({ anomalies }: { anomalies: AnomalyLine[] }) {
  return (
    <DataTable
      rows={anomalies}
      pageSize={50}
      emptyMessage="Aucune anomalie sur cette période."
      columns={[
        { header: "FirstName", accessor: (a) => a.firstName },
        { header: "LastName", accessor: (a) => a.lastName },
        { header: "Identifier", accessor: (a) => a.identifier },
        { header: "RegistrationNumber", accessor: (a) => a.registrationNumber ?? "" },
        { header: "Company", accessor: (a) => a.company },
        { header: "period", accessor: (a) => a.period },
        { header: "OrganizationalUnit", accessor: (a) => a.organizationalUnit },
        { header: "product", accessor: (a) => a.product },
        { header: "ActivityNature", accessor: (a) => a.activityNature },
        { header: "AccountingCode", accessor: () => "" },
        { header: "Ratio", accessor: () => "" },
      ]}
    />
  );
}
