import { useEffect, useState } from "react";
import { Radio, Droplets, Trash2, Upload } from "lucide-react";
import {
  getZonas,
  consultarSenamhi,
  importarSenamhi,
  importarCsvAna,
  getCalidadAgua,
  registrarReporteResiduos,
  getReportesResiduos,
} from "../services/monitoreoService";
import NivelBadge from "../components/NivelBadge";

export default function Integraciones() {
  const [zonas, setZonas] = useState([]);

  useEffect(() => {
    getZonas().then(setZonas).catch(() => {});
  }, []);

  return (
    <div>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 20px" }}>
        Conectores con fuentes externas (Capítulo 8.4). Al no contar con acceso a las APIs
        reales, cada conector replica el formato de la fuente correspondiente para validar la
        arquitectura de integración.
      </p>

      <div style={{ display: "grid", gap: 24 }}>
        <PanelSenamhi zonas={zonas} />
        <PanelAna />
        <PanelMunicipalidad zonas={zonas} />
      </div>
    </div>
  );
}

function PanelSenamhi({ zonas }) {
  const [zonaId, setZonaId] = useState("");
  const [preview, setPreview] = useState(null);
  const [mensaje, setMensaje] = useState(null);
  const [cargando, setCargando] = useState(false);

  const consultar = async () => {
    if (!zonaId) return;
    setCargando(true);
    setMensaje(null);
    try {
      setPreview(await consultarSenamhi(zonaId));
    } finally {
      setCargando(false);
    }
  };

  const importar = async () => {
    setCargando(true);
    try {
      await importarSenamhi(zonaId);
      setMensaje("Dato importado a datos_ambientales correctamente.");
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="card">
      <h2><Radio size={18} color="var(--agua)" /> SENAMHI — calidad del aire (mock REST)</h2>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 12px" }}>
        Simula el consumo del API de SENAMHI. Selecciona una zona para ver el formato de
        respuesta esperado y, si corresponde, importarlo como dato ambiental.
      </p>
      <div style={{ display: "flex", gap: 12, alignItems: "flex-end", marginBottom: 12 }}>
        <div style={{ flex: 1, maxWidth: 260 }}>
          <label>Zona</label>
          <select value={zonaId} onChange={(e) => { setZonaId(e.target.value); setPreview(null); }}>
            <option value="">Seleccionar...</option>
            {zonas.map((z) => (
              <option key={z.id} value={z.id}>{z.nombre}</option>
            ))}
          </select>
        </div>
        <button onClick={consultar} disabled={!zonaId || cargando}>Consultar mock</button>
        {preview && (
          <button className="primary" onClick={importar} disabled={cargando}>
            <Upload size={14} /> Importar a datos ambientales
          </button>
        )}
      </div>

      {preview && (
        <pre
          style={{
            background: "var(--surface-2)",
            padding: 12,
            borderRadius: 8,
            fontSize: 12,
            overflowX: "auto",
          }}
        >
{JSON.stringify(preview, null, 2)}
        </pre>
      )}
      {mensaje && <p style={{ fontSize: 13, color: "var(--accent)" }}>{mensaje}</p>}
    </div>
  );
}

function PanelAna() {
  const [archivo, setArchivo] = useState(null);
  const [registros, setRegistros] = useState([]);
  const [mensaje, setMensaje] = useState(null);
  const [cargando, setCargando] = useState(false);

  const cargar = () => getCalidadAgua().then(setRegistros).catch(() => {});

  useEffect(() => {
    cargar();
  }, []);

  const subir = async () => {
    if (!archivo) return;
    setCargando(true);
    setMensaje(null);
    try {
      const res = await importarCsvAna(archivo);
      setMensaje(`Se importaron ${res.length} registro(s) de calidad de agua.`);
      cargar();
    } catch {
      setMensaje("Error al procesar el CSV. Verifica el formato: cuenca,ph,turbidez,coliformes,fecha");
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="card">
      <h2><Droplets size={18} color="var(--agua)" /> ANA — calidad del agua (carga CSV)</h2>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 12px" }}>
        Formato esperado del CSV (con encabezado): <code>cuenca,ph,turbidez,coliformes,fecha</code>
      </p>
      <div style={{ display: "flex", gap: 12, alignItems: "center", marginBottom: 12 }}>
        <input type="file" accept=".csv" onChange={(e) => setArchivo(e.target.files[0])} style={{ flex: 1 }} />
        <button className="primary" onClick={subir} disabled={!archivo || cargando}>
          <Upload size={14} /> {cargando ? "Procesando..." : "Importar CSV"}
        </button>
      </div>
      {mensaje && <p style={{ fontSize: 13, color: "var(--text-muted)" }}>{mensaje}</p>}

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
        <thead>
          <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
            <th style={{ padding: "6px 8px" }}>Cuenca</th>
            <th style={{ padding: "6px 8px" }}>pH</th>
            <th style={{ padding: "6px 8px" }}>Turbidez (NTU)</th>
            <th style={{ padding: "6px 8px" }}>Coliformes fecales</th>
            <th style={{ padding: "6px 8px" }}>Fecha</th>
          </tr>
        </thead>
        <tbody>
          {registros.map((r) => (
            <tr key={r.id} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{r.cuenca}</td>
              <td style={{ padding: "6px 8px" }}>{r.ph}</td>
              <td style={{ padding: "6px 8px" }}>{r.turbidezNtu}</td>
              <td style={{ padding: "6px 8px" }}>{r.coliformesFecales}</td>
              <td style={{ padding: "6px 8px" }}>{r.fechaMuestreo}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PanelMunicipalidad({ zonas }) {
  const inicial = { zonaId: "", descripcion: "", nivelRiesgo: "MEDIO", reportadoPor: "" };
  const [form, setForm] = useState(inicial);
  const [reportes, setReportes] = useState([]);
  const [guardando, setGuardando] = useState(false);

  const cargar = () => getReportesResiduos().then(setReportes).catch(() => {});

  useEffect(() => {
    cargar();
  }, []);

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const guardar = async (e) => {
    e.preventDefault();
    setGuardando(true);
    try {
      await registrarReporteResiduos({
        ...form,
        zonaId: form.zonaId ? Number(form.zonaId) : null,
      });
      setForm(inicial);
      cargar();
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div className="card">
      <h2><Trash2 size={18} color="var(--tierra)" /> Municipalidad de Ica — residuos sólidos (carga manual)</h2>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 12px" }}>
        Sin API disponible: el personal administrativo registra manualmente las zonas de riesgo
        reportadas por la Municipalidad.
      </p>

      <form onSubmit={guardar} style={{ marginBottom: 16 }}>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: 12 }}>
          <div>
            <label>Zona</label>
            <select value={form.zonaId} onChange={(e) => set("zonaId", e.target.value)}>
              <option value="">(sin zona)</option>
              {zonas.map((z) => (
                <option key={z.id} value={z.id}>{z.nombre}</option>
              ))}
            </select>
          </div>
          <div>
            <label>Nivel de riesgo</label>
            <select value={form.nivelRiesgo} onChange={(e) => set("nivelRiesgo", e.target.value)}>
              <option value="BAJO">Bajo</option>
              <option value="MEDIO">Medio</option>
              <option value="ALTO">Alto</option>
              <option value="CRITICO">Crítico</option>
            </select>
          </div>
          <div>
            <label>Reportado por</label>
            <input value={form.reportadoPor} onChange={(e) => set("reportadoPor", e.target.value)} />
          </div>
          <div style={{ gridColumn: "1 / -1" }}>
            <label>Descripción</label>
            <input required value={form.descripcion} onChange={(e) => set("descripcion", e.target.value)} />
          </div>
        </div>
        <button className="primary" type="submit" disabled={guardando} style={{ marginTop: 12 }}>
          Registrar reporte
        </button>
      </form>

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
        <thead>
          <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
            <th style={{ padding: "6px 8px" }}>Zona</th>
            <th style={{ padding: "6px 8px" }}>Nivel</th>
            <th style={{ padding: "6px 8px" }}>Descripción</th>
            <th style={{ padding: "6px 8px" }}>Fecha</th>
            <th style={{ padding: "6px 8px" }}>Reportado por</th>
          </tr>
        </thead>
        <tbody>
          {reportes.map((r) => (
            <tr key={r.id} style={{ borderTop: "1px solid var(--border)" }}>
              <td style={{ padding: "6px 8px" }}>{r.zona?.nombre ?? "-"}</td>
              <td style={{ padding: "6px 8px" }}><NivelBadge nivel={r.nivelRiesgo} /></td>
              <td style={{ padding: "6px 8px" }}>{r.descripcion}</td>
              <td style={{ padding: "6px 8px" }}>{r.fechaReporte}</td>
              <td style={{ padding: "6px 8px" }}>{r.reportadoPor ?? "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
