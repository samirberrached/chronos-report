import type { AnomalyLine, ReportLine } from "../types/report";

export interface NamedValue {
  name: string;
  value: number;
}

const UNSPECIFIED = "Non renseigné";

export function groupSum<T>(items: T[], key: (item: T) => string | null, value: (item: T) => number): NamedValue[] {
  const totals = new Map<string, number>();
  for (const item of items) {
    const k = key(item) ?? UNSPECIFIED;
    totals.set(k, (totals.get(k) ?? 0) + value(item));
  }
  return [...totals.entries()].map(([name, v]) => ({ name, value: Math.round(v * 100) / 100 }));
}

export function groupCount<T>(items: T[], key: (item: T) => string | null): NamedValue[] {
  const counts = new Map<string, number>();
  for (const item of items) {
    const k = key(item) ?? UNSPECIFIED;
    counts.set(k, (counts.get(k) ?? 0) + 1);
  }
  return [...counts.entries()].map(([name, value]) => ({ name, value }));
}

export function groupCountDistinct<T>(
  items: T[],
  key: (item: T) => string | null,
  idOf: (item: T) => string
): NamedValue[] {
  const groups = new Map<string, Set<string>>();
  for (const item of items) {
    const k = key(item) ?? UNSPECIFIED;
    if (!groups.has(k)) groups.set(k, new Set());
    groups.get(k)!.add(idOf(item));
  }
  return [...groups.entries()].map(([name, ids]) => ({ name, value: ids.size }));
}

export function groupAverage<T>(items: T[], key: (item: T) => string | null, value: (item: T) => number): NamedValue[] {
  const totals = new Map<string, { sum: number; count: number }>();
  for (const item of items) {
    const k = key(item) ?? UNSPECIFIED;
    const entry = totals.get(k) ?? { sum: 0, count: 0 };
    entry.sum += value(item);
    entry.count += 1;
    totals.set(k, entry);
  }
  return [...totals.entries()].map(([name, { sum, count }]) => ({
    name,
    value: Math.round((sum / count) * 100) / 100,
  }));
}

/** Répartition croisée : lignes × colonnes, valeurs = somme. Pour tableaux croisés / bar charts empilés. */
export function crossTabSum<T>(
  items: T[],
  rowKey: (item: T) => string | null,
  colKey: (item: T) => string,
  value: (item: T) => number
): { row: string; values: Record<string, number> }[] {
  const rows = new Map<string, Record<string, number>>();
  for (const item of items) {
    const r = rowKey(item) ?? UNSPECIFIED;
    const c = colKey(item);
    if (!rows.has(r)) rows.set(r, {});
    const bucket = rows.get(r)!;
    // Accumuler en pleine précision, arrondir une seule fois à la fin (voir plus bas) :
    // arrondir à chaque addition dérive sur les cellules mises à jour beaucoup de fois.
    bucket[c] = (bucket[c] ?? 0) + value(item);
  }
  return [...rows.entries()].map(([row, values]) => {
    const rounded: Record<string, number> = {};
    for (const [col, v] of Object.entries(values)) rounded[col] = Math.round(v * 100) / 100;
    return { row, values: rounded };
  });
}

/**
 * Ratio est un pourcentage de temps PAR LIGNE (borné 0-100). Sommer Ratio sur plusieurs
 * lignes/employés dépasse mécaniquement 100 % et n'a plus de sens affiché en "%". Plutôt que
 * de montrer ce cumul brut, on exprime chaque catégorie comme sa PART du total (borné 0-100,
 * la somme de toutes les parts fait 100 %) — un vrai pourcentage, comparable et lisible.
 */
export function shareOfTotal(items: NamedValue[]): NamedValue[] {
  const total = items.reduce((sum, i) => sum + i.value, 0);
  if (total === 0) return items.map((i) => ({ name: i.name, value: 0 }));
  return items.map((i) => ({ name: i.name, value: Math.round((i.value / total) * 1000) / 10 }));
}

/**
 * Pour une ventilation croisée (ex : par unité organisationnelle × type d'allocation),
 * normalise CHAQUE ligne à 100 % — montre la composition propre à cette ligne plutôt qu'une
 * part du total global, plus pertinent pour comparer la répartition entre catégories de
 * tailles très différentes.
 */
export function normalizeRowsToPercent<R extends { row: string; values: Record<string, number> }>(rows: R[]): R[] {
  return rows.map((r) => {
    const total = Object.values(r.values).reduce((sum, v) => sum + v, 0);
    if (total === 0) return r;
    const values: Record<string, number> = {};
    for (const [key, v] of Object.entries(r.values)) {
      values[key] = Math.round((v / total) * 1000) / 10;
    }
    return { ...r, values };
  });
}

export const NO_TS_PREFIX = "NO_TS_";
export const NA_CODE = "NA";
export const NOT_FOUND = "NOT FOUND";

export function isTimesheetBacked(line: ReportLine): boolean {
  return line.accountingCode !== null && line.accountingCode !== NA_CODE && !line.accountingCode.startsWith(NO_TS_PREFIX);
}

export const ACCOUNTING_BUCKET_ALLOCATED = "Code alloué";
export const ACCOUNTING_BUCKET_NO_TS = "Sans timesheet (NO_TS_*)";
export const ACCOUNTING_BUCKET_LEAVE = "Congé (NA)";

/**
 * AccountingCode est quasi-unique par ligne (code de facturation réel) sauf pour deux
 * marqueurs : NO_TS_<région> (pas de timesheet) et NA (congé complet). Regrouper par la
 * valeur brute produirait des milliers de catégories ; on regroupe donc par nature.
 */
export function bucketAccountingCode(code: string | null): string {
  if (code === null) return UNSPECIFIED;
  if (code === NA_CODE) return ACCOUNTING_BUCKET_LEAVE;
  if (code.startsWith(NO_TS_PREFIX)) return ACCOUNTING_BUCKET_NO_TS;
  return ACCOUNTING_BUCKET_ALLOCATED;
}

const MISSING_FIELD_LABELS: { key: keyof AnomalyLine; label: string }[] = [
  { key: "organizationalUnit", label: "Unité organisationnelle" },
  { key: "product", label: "Produit" },
  { key: "activityNature", label: "Nature d'activité" },
];

export function missingFields(anomaly: AnomalyLine): string[] {
  return MISSING_FIELD_LABELS.filter((f) => anomaly[f.key] === NOT_FOUND).map((f) => f.label);
}

/** Une anomalie peut avoir plusieurs champs manquants ; chacun compte dans sa propre catégorie. */
export function anomalyTypeBreakdown(anomalies: AnomalyLine[]): NamedValue[] {
  const counts = new Map<string, number>();
  for (const a of anomalies) {
    for (const field of missingFields(a)) {
      counts.set(field, (counts.get(field) ?? 0) + 1);
    }
  }
  return [...counts.entries()].map(([name, value]) => ({ name, value }));
}
