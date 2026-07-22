import { useState } from "react";
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { ValueType } from "recharts/types/component/DefaultTooltipContent";
import { DataTable } from "../DataTable";
import { CATEGORICAL_DARK, CATEGORICAL_LIGHT } from "./palette";
import { useIsDarkMode } from "./useColorScheme";

export interface StackedBarDatum {
  name: string;
  [series: string]: string | number;
}

interface StackedBarChartCardProps {
  title: string;
  data: StackedBarDatum[];
  series: string[];
  maxRows?: number;
  valueFormatter?: (value: number) => string;
}

// Bar chart empilé = plusieurs séries par catégorie : palette catégorielle (identité des
// séries) + légende, comme le pie chart. Une seule teinte n'aurait pas de sens ici.
export function StackedBarChartCard({ title, data, series, maxRows = 12, valueFormatter }: StackedBarChartCardProps) {
  const isDark = useIsDarkMode();
  const colors = isDark ? CATEGORICAL_DARK : CATEGORICAL_LIGHT;
  const gridColor = isDark ? "#2c2c2a" : "#e1e0d9";
  const textColor = "#898781";
  const [showAll, setShowAll] = useState(false);

  const withTotals = data.map((d) => ({
    ...d,
    __total: series.reduce((sum, key) => sum + (Number(d[key]) || 0), 0),
  }));
  const sorted = [...withTotals].sort((a, b) => b.__total - a.__total);
  const visible = sorted.slice(0, maxRows);
  const omitted = sorted.length - visible.length;

  return (
    <div className="panel">
      <h2>{title}</h2>
      {visible.length === 0 ? (
        <div className="empty-state">Aucune donnée</div>
      ) : (
        <>
          <ResponsiveContainer width="100%" height={Math.max(280, visible.length * 30)}>
            <BarChart data={visible} layout="vertical" margin={{ left: 8, right: 24 }}>
              <CartesianGrid stroke={gridColor} horizontal={false} />
              <XAxis type="number" tick={{ fill: textColor, fontSize: 12 }} />
              <YAxis type="category" dataKey="name" width={140} interval={0} tick={{ fill: textColor, fontSize: 12 }} />
              <Tooltip
                formatter={(value: ValueType | undefined) =>
                  valueFormatter ? valueFormatter(Number(value)) : value
                }
                contentStyle={{ fontSize: 13 }}
              />
              <Legend wrapperStyle={{ fontSize: 12 }} />
              {series.map((key, i) => (
                <Bar key={key} dataKey={key} stackId="stack" fill={colors[i % colors.length]} maxBarSize={24} />
              ))}
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
                ...series.map((key) => ({
                  header: key,
                  accessor: (r: StackedBarDatum & { __total: number }) => {
                    const v = Number(r[key]) || 0;
                    return valueFormatter ? valueFormatter(v) : v;
                  },
                })),
              ]}
            />
          )}
        </>
      )}
    </div>
  );
}
