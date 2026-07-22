import { useEffect, useState } from "react";
import { Flame } from "lucide-react";
import { getMapaCalorDengue } from "../services/monitoreoService";
import HeatmapDengue from "../components/HeatmapDengue";

export default function MapaCalor() {
  const [data, setData] = useState(null);
  const [anio, setAnio] = useState("");

  useEffect(() => {
    getMapaCalorDengue(anio || null).then(setData).catch(() => {});
  }, [anio]);

  const puntos = data?.puntos ?? [];
  const anios = data?.meta?.anios ?? [];
  const total = data?.meta?.totalCasos ?? 0;

  return (
    <div>
      <div className="card" style={{ marginBottom: 24 }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 12 }}>
          <h2><Flame size={18} color="var(--critico)" /> Mapa de calor — Casos de dengue en Ica</h2>
          <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
            <label style={{ margin: 0 }}>Año:</label>
            <select value={anio} onChange={(e) => setAnio(e.target.value)} style={{ width: 160 }}>
              <option value="">Todos (2015–2024)</option>
              {anios.map((a) => (
                <option key={a} value={a}>{a}</option>
              ))}
            </select>
          </div>
        </div>
        <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "4px 0 0" }}>
          Concentración de casos de dengue por distrito. Datos reales de vigilancia
          epidemiológica del MINSA · <strong>{total.toLocaleString()}</strong> casos
          {anio ? ` en ${anio}` : " (2015–2024)"} · {puntos.length} distritos.
        </p>
      </div>

      <div className="card" style={{ marginBottom: 24 }}>
        <HeatmapDengue puntos={puntos} />
      </div>

      <div className="card">
        <h2><Flame size={18} color="var(--tierra)" /> Distritos con más casos</h2>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
              <th style={{ padding: "6px 8px" }}>Distrito</th>
              <th style={{ padding: "6px 8px" }}>Provincia</th>
              <th style={{ padding: "6px 8px" }}>Casos{anio ? ` (${anio})` : ""}</th>
            </tr>
          </thead>
          <tbody>
            {puntos.slice(0, 12).map((p) => (
              <tr key={p.ubigeo} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>{p.distrito}</td>
                <td style={{ padding: "6px 8px" }}>{p.provincia}</td>
                <td style={{ padding: "6px 8px", fontWeight: 600 }}>{p.casos.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
