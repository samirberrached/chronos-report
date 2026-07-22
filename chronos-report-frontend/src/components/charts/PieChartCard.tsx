import { Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";
import type { ValueType } from "recharts/types/component/DefaultTooltipContent";
import { CATEGORICAL_DARK, CATEGORICAL_LIGHT, MAX_PIE_SLICES, OTHER_LABEL } from "./palette";
import { useIsDarkMode } from "./useColorScheme";

export interface PieDatum {
  name: string;
  value: number;
}

interface PieChartCardProps {
  title: string;
  data: PieDatum[];
  valueFormatter?: (value: number) => string;
}

// Pie chart = part-to-whole, l'identité EST la couleur : palette catégorielle
// validée (validate_palette.js), ordre figé, repliée à 6 tranches + "Autres" max.
export function PieChartCard({ title, data, valueFormatter }: PieChartCardProps) {
  const isDark = useIsDarkMode();
  const colors = isDark ? CATEGORICAL_DARK : CATEGORICAL_LIGHT;

  const sorted = [...data].sort((a, b) => b.value - a.value);
  const visible = sorted.slice(0, MAX_PIE_SLICES);
  const rest = sorted.slice(MAX_PIE_SLICES);
  const restTotal = rest.reduce((sum, d) => sum + d.value, 0);
  const slices = restTotal > 0 ? [...visible, { name: OTHER_LABEL, value: restTotal }] : visible;

  const total = slices.reduce((sum, d) => sum + d.value, 0);

  return (
    <div className="panel">
      <h2>{title}</h2>
      {slices.length === 0 ? (
        <div className="empty-state">Aucune donnée</div>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={slices}
              dataKey="value"
              nameKey="name"
              innerRadius={60}
              outerRadius={100}
              paddingAngle={2}
              label={({ name, value }) => `${name} ${((value / total) * 100).toFixed(0)}%`}
              labelLine={false}
            >
              {slices.map((entry, i) => (
                <Cell key={entry.name} fill={colors[i % colors.length]} />
              ))}
            </Pie>
            <Legend wrapperStyle={{ fontSize: 12 }} />
            <Tooltip
              formatter={(value: ValueType | undefined) =>
                valueFormatter ? valueFormatter(Number(value)) : Number(value).toLocaleString("fr-FR")
              }
            />
          </PieChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
