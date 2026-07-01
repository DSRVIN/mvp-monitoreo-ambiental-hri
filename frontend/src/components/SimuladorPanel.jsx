import { useState } from "react";
import { FlaskConical, Trash2, TriangleAlert } from "lucide-react";
import { simularDatos, limpiarSimulados } from "../services/monitoreoService";

const initial = {
  cantidad: 50,
  impacto: 5,
  radioKm: 8,
  generarAlertas: true,
};

export default function SimuladorPanel({ onCambio }) {
  const [form, setForm] = useState(initial);
  const [cargando, setCargando] = useState(false);
  const [mensaje, setMensaje] = useState(null);

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const generar = async () => {
    setCargando(true);
    setMensaje(null);
    try {
      const res = await simularDatos({
        cantidad: Number(form.cantidad),
        impacto: Number(form.impacto),
        radioKm: Number(form.radioKm),
        generarAlertas: form.generarAlertas,
      });
      setMensaje(`Se generaron ${res.generados} puntos de datos.`);
      onCambio?.();
    } catch {
      setMensaje("Error al generar datos.");
    } finally {
      setCargando(false);
    }
  };

  const limpiar = async () => {
    setCargando(true);
    setMensaje(null);
    try {
      const res = await limpiarSimulados();
      setMensaje(`Se eliminaron ${res.eliminados} puntos simulados.`);
      onCambio?.();
    } catch {
      setMensaje("Error al limpiar datos.");
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="card">
      <h2><FlaskConical size={18} color="var(--tierra)" /> Simulador de datos</h2>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 16px" }}>
        Genera datos ambientales imaginarios para poblar el mapa y las estadísticas.
      </p>

      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
          gap: 16,
          marginBottom: 16,
        }}
      >
        <div>
          <label>Cantidad de puntos: {form.cantidad}</label>
          <input
            type="range"
            min="5"
            max="500"
            step="5"
            value={form.cantidad}
            onChange={(e) => set("cantidad", e.target.value)}
          />
        </div>

        <div>
          <label>Impacto / contaminación: {form.impacto} / 10</label>
          <input
            type="range"
            min="1"
            max="10"
            value={form.impacto}
            onChange={(e) => set("impacto", e.target.value)}
          />
        </div>

        <div>
          <label>Dispersión (radio km): {form.radioKm}</label>
          <input
            type="range"
            min="1"
            max="50"
            value={form.radioKm}
            onChange={(e) => set("radioKm", e.target.value)}
          />
        </div>

        <div style={{ display: "flex", alignItems: "flex-end" }}>
          <label style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 8 }}>
            <input
              type="checkbox"
              style={{ width: "auto" }}
              checked={form.generarAlertas}
              onChange={(e) => set("generarAlertas", e.target.checked)}
            />
            <TriangleAlert size={14} /> Generar alertas críticas
          </label>
        </div>
      </div>

      <div style={{ display: "flex", gap: 12, alignItems: "center" }}>
        <button className="primary" onClick={generar} disabled={cargando}>
          <FlaskConical size={14} /> {cargando ? "Procesando..." : "Generar datos"}
        </button>
        <button onClick={limpiar} disabled={cargando}>
          <Trash2 size={14} /> Limpiar simulados
        </button>
        {mensaje && <span style={{ fontSize: 13, color: "var(--text-muted)" }}>{mensaje}</span>}
      </div>
    </div>
  );
}
