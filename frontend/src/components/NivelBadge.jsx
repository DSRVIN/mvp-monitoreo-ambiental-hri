import { Leaf, Cloud, Wind, Biohazard } from "lucide-react";

const CONFIG = {
  BAJO: { color: "var(--bajo)", fondo: "#e3f5e9", icono: Leaf, label: "Bajo" },
  MEDIO: { color: "var(--medio)", fondo: "#fbf1da", icono: Cloud, label: "Medio" },
  ALTO: { color: "var(--alto)", fondo: "#fce6d8", icono: Wind, label: "Alto" },
  CRITICO: { color: "var(--critico)", fondo: "#fbe0e0", icono: Biohazard, label: "Crítico" },
};

export default function NivelBadge({ nivel }) {
  const cfg = CONFIG[nivel] || { color: "#888", fondo: "#eee", icono: Cloud, label: nivel ?? "N/D" };
  const Icono = cfg.icono;
  return (
    <span
      style={{
        display: "inline-flex",
        alignItems: "center",
        gap: 6,
        padding: "3px 10px",
        borderRadius: 999,
        fontSize: 12,
        fontWeight: 600,
        color: cfg.color,
        background: cfg.fondo,
      }}
    >
      <Icono size={13} />
      {cfg.label}
    </span>
  );
}
