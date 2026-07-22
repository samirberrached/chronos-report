import { useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import type { ValueType } from "recharts/types/component/DefaultTooltipContent";
import { DataTable } from "../DataTable";
import { SEQUENTIAL_HUE } from "./palette";
import { useIsDarkMode } from "./useColorScheme";

export interface BarDatum {
  name: string;
  value: number;
}

interface BarChartCardProps {
  title: string;
  data: BarDatum[];
  maxBars?: number;
  valueFormatter?: (value: number) => string;
}

// Bar chart = comparaison de magnitude sur UNE seule mesure : une seule teinte
// (rouge VERMEG), pas de palette catégorielle — la position/l'étiquette porte déjà l'identité.
export function BarChartCard({ title, data, maxBars = 10, valueFormatter }: BarChartCardProps) {
  const isDark = useIsDarkMode();
  const gridColor = isDark ? "#2c2c2a" : "#e1e0d9";
  const textColor = "#898781";
  const [showAll, setShowAll] = useState(false);

  const sorted = [...data].sort((a, b) => b.value - a.value);
  const visible = sorted.slice(0, maxBars);
  const omitted = sorted.length - visible.length;

  return (
    <div className="panel">
      <h2>{title}</h2>
      {visible.length === 0 ? (
        <div className="empty-state">Aucune donnée</div>
      ) : (
        <>
          <ResponsiveContainer width="100%" height={Math.max(280, visible.length * 32)}>
            <BarChart data={visible} layout="vertical" margin={{ left: 8, right: 24 }}>
              <CartesianGrid stroke={gridColor} horizontal={false} />
              <XAxis type="number" tick={{ fill: textColor, fontSize: 12 }} />
              <YAxis
                type="category"
                dataKey="name"
                width={140}
                interval={0}
                tick={{ fill: textColor, fontSize: 12 }}
              />
              <Tooltip
                formatter={(value: ValueType | undefined) =>
                  valueFormatter ? valueFormatter(Number(value)) : value
                }
                contentStyle={{ fontSize: 13 }}
              />
              <Bar dataKey="value" fill={SEQUENTIAL_HUE} radius={[0, 4, 4, 0]} maxBarSize={24} />
            </BarChart>
          </ResponsiveContainer>
          {omitted > 0 && (
            <button className="chart-toggle" onClick={() => setShowAll((v) => !v)}>
              {showAll ? "Masquer le tableau détaillé" : `+ ${omitted} autres (voir le tableau détaillé)`}
            </button>
          )}
          {showAll && (
            <DataTable
              rows={sorted}
              pageSize={50}
              columns={[
                { header: "Nom", accessor: (r) => r.name },
                { header: "Valeur", accessor: (r) => (valueFormatter ? valueFormatter(r.value) : r.value) },
              ]}
            />
          )}
        </>
      )}
    </div>
  );
}
