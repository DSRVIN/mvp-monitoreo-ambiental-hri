import { useEffect, useState } from "react";
import { MapPinned, RefreshCw, Download } from "lucide-react";
import { getZonasCriticas, ejecutarAnalisis, descargarReporteZonasCriticas } from "../services/monitoreoService";
import NivelBadge from "./NivelBadge";

export default function ZonasCriticasPanel({ onCambio }) {
  const [zonas, setZonas] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [exportando, setExportando] = useState(false);
  const [mensaje, setMensaje] = useState(null);

  const cargar = () => {
    getZonasCriticas().then(setZonas).catch(() => {});
  };

  useEffect(() => {
    cargar();
  }, []);

  const ejecutar = async () => {
    setCargando(true);
    setMensaje(null);
    try {
      const nuevas = await ejecutarAnalisis();
      setMensaje(
        nuevas.length > 0
          ? `Análisis ejecutado: se generaron ${nuevas.length} alerta(s) nueva(s).`
          : "Análisis ejecutado: ninguna zona superó el umbral de riesgo."
      );
      cargar();
      onCambio?.();
    } catch {
      setMensaje("Error al ejecutar el análisis.");
    } finally {
      setCargando(false);
    }
  };

  const exportar = async () => {
    setExportando(true);
    try {
      await descargarReporteZonasCriticas();
    } finally {
      setExportando(false);
    }
  };

  return (
    <div className="card">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 12 }}>
        <h2><MapPinned size={18} color="var(--agua)" /> Zonas críticas (Servicio de Análisis)</h2>
        <div style={{ display: "flex", gap: 8 }}>
          <button onClick={exportar} disabled={exportando}>
            <Download size={14} /> {exportando ? "Exportando..." : "Exportar CSV"}
          </button>
          <button className="primary" onClick={ejecutar} disabled={cargando}>
            <RefreshCw size={14} className={cargando ? "girando" : ""} />
            {cargando ? "Analizando..." : "Ejecutar análisis ahora"}
          </button>
        </div>
      </div>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 12px" }}>
        Correlaciona diagnósticos por distrito (últimos 7 días) con la calidad del aire de la
        zona. Umbral de alerta: más de 5 casos en 7 días.
      </p>

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
        <thead>
          <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
            <th style={{ padding: "6px 8px" }}>Zona</th>
            <th style={{ padding: "6px 8px" }}>Nivel de riesgo</th>
            <th style={{ padding: "6px 8px" }}>Casos (7 días)</th>
            <th style={{ padding: "6px 8px" }}>ICA promedio</th>
          </tr>
        </thead>
        <tbody>
          {zonas.map((z) => (
            <tr key={z.zonaId} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{z.nombre}</td>
              <td style={{ padding: "6px 8px" }}><NivelBadge nivel={z.nivelRiesgo} /></td>
              <td style={{ padding: "6px 8px" }}>{z.casosUltimos7Dias}</td>
              <td style={{ padding: "6px 8px" }}>{z.icaPromedio ?? "N/D"}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {mensaje && (
        <p style={{ fontSize: 13, color: "var(--text-muted)", marginTop: 12 }}>{mensaje}</p>
      )}
    </div>
  );
}
