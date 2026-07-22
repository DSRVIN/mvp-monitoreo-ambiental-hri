import { useEffect, useState } from "react";
import { MapContainer, TileLayer, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet.heat";
import { Flame } from "lucide-react";
import { getMapaCalorDengue } from "../services/monitoreoService";

// Capa de calor: agrega/quita una L.heatLayer sobre el mapa segun los puntos.
function CapaCalor({ puntos }) {
  const map = useMap();

  useEffect(() => {
    if (!puntos.length) return;
    const maxCasos = Math.max(...puntos.map((p) => p.casos));
    const datos = puntos.map((p) => [p.lat, p.lng, p.casos]);
    const capa = L.heatLayer(datos, {
      radius: 35,
      blur: 25,
      max: maxCasos,
      minOpacity: 0.35,
      gradient: { 0.2: "#2ecc71", 0.4: "#f1c40f", 0.6: "#e67e22", 0.8: "#e74c3c", 1.0: "#a80000" },
    }).addTo(map);
    return () => map.removeLayer(capa);
  }, [puntos, map]);

  return null;
}

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
        <MapContainer center={[-13.9, -75.75]} zoom={9} style={{ height: 460, width: "100%", borderRadius: 10 }}>
          <TileLayer
            attribution='&copy; OpenStreetMap contributors &copy; CARTO'
            url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
          />
          <CapaCalor puntos={puntos} />
        </MapContainer>
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap", marginTop: 10, fontSize: 12, color: "var(--text-muted)" }}>
          <span>Intensidad del color = número de casos:</span>
          {[
            { c: "#2ecc71", t: "Bajo" }, { c: "#f1c40f", t: "Moderado" },
            { c: "#e67e22", t: "Alto" }, { c: "#e74c3c", t: "Muy alto" }, { c: "#a80000", t: "Crítico" },
          ].map((l) => (
            <span key={l.t} style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 12, height: 12, borderRadius: 3, background: l.c, display: "inline-block" }} />
              {l.t}
            </span>
          ))}
        </div>
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
