import { useMemo, useState } from "react";

interface Column<T> {
  header: string;
  accessor: (row: T) => string | number;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  rows: T[];
  emptyMessage?: string;
  /** Si fourni, la table se pagine dès que le nombre de lignes (après filtre) le dépasse. */
  pageSize?: number;
}

type SortState = { columnIndex: number; direction: "asc" | "desc" } | null;

export function DataTable<T>({ columns, rows, emptyMessage = "Aucune donnée", pageSize }: DataTableProps<T>) {
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState<SortState>(null);
  const [page, setPage] = useState(0);

  const filtered = useMemo(() => {
    const query = search.trim().toLowerCase();
    if (!query) return rows;
    return rows.filter((row) => columns.some((col) => String(col.accessor(row)).toLowerCase().includes(query)));
  }, [rows, search, columns]);

  const sorted = useMemo(() => {
    if (!sort) return filtered;
    const accessor = columns[sort.columnIndex].accessor;
    const factor = sort.direction === "asc" ? 1 : -1;
    return [...filtered].sort((a, b) => {
      const va = accessor(a);
      const vb = accessor(b);
      if (typeof va === "number" && typeof vb === "number") return (va - vb) * factor;
      return String(va).localeCompare(String(vb)) * factor;
    });
  }, [filtered, sort, columns]);

  const totalPages = pageSize ? Math.max(1, Math.ceil(sorted.length / pageSize)) : 1;
  const currentPage = Math.min(page, totalPages - 1);
  const pageRows = pageSize ? sorted.slice(currentPage * pageSize, currentPage * pageSize + pageSize) : sorted;

  function toggleSort(columnIndex: number) {
    setSort((prev) => {
      if (!prev || prev.columnIndex !== columnIndex) return { columnIndex, direction: "asc" };
      if (prev.direction === "asc") return { columnIndex, direction: "desc" };
      return null;
    });
  }

  function handleSearchChange(value: string) {
    setSearch(value);
    setPage(0);
  }

  if (rows.length === 0) {
    return <div className="empty-state">{emptyMessage}</div>;
  }

  return (
    <div>
      <input
        className="input table-filter"
        type="text"
        placeholder="Filtrer…"
        value={search}
        onChange={(e) => handleSearchChange(e.target.value)}
      />

      {sorted.length === 0 ? (
        <div className="empty-state">Aucun résultat pour « {search} ».</div>
      ) : (
        <>
          <div className="table-scroll">
            <table className="data-table">
              <thead>
                <tr>
                  {columns.map((col, i) => (
                    <th key={col.header} className="sortable" onClick={() => toggleSort(i)}>
                      {col.header}
                      {sort?.columnIndex === i && <span> {sort.direction === "asc" ? "▲" : "▼"}</span>}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {pageRows.map((row, i) => (
                  <tr key={i}>
                    {columns.map((col) => (
                      <td key={col.header}>{col.accessor(row)}</td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {pageSize && totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-secondary"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={currentPage === 0}
              >
                ← Précédent
              </button>
              <span>
                Page {currentPage + 1} / {totalPages} · {sorted.length} lignes
              </span>
              <button
                className="btn btn-secondary"
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={currentPage >= totalPages - 1}
              >
                Suivant →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
