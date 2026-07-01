import { ChevronLeft, ChevronRight } from "lucide-react";

// page: objeto Page de Spring Data (number, totalPages, totalElements)
export default function Paginador({ page, onCambiar }) {
  if (!page || page.totalPages <= 1) return null;

  const actual = page.number; // base 0
  const total = page.totalPages;

  return (
    <div style={{ display: "flex", alignItems: "center", justifyContent: "flex-end", gap: 12, marginTop: 12 }}>
      <span style={{ fontSize: 13, color: "var(--text-muted)" }}>
        Página {actual + 1} de {total} · {page.totalElements} registros
      </span>
      <button onClick={() => onCambiar(actual - 1)} disabled={actual === 0}>
        <ChevronLeft size={14} /> Anterior
      </button>
      <button onClick={() => onCambiar(actual + 1)} disabled={actual >= total - 1}>
        Siguiente <ChevronRight size={14} />
      </button>
    </div>
  );
}
