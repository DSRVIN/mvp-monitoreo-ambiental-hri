import { useEffect, useState } from "react";
import { TriangleAlert, CircleCheck, Siren, Download } from "lucide-react";
import {
  getAlertas,
  crearAlerta,
  atenderAlerta,
  getZonas,
  getEnfermedades,
  descargarReporteAlertas,
} from "../services/monitoreoService";
import NivelBadge from "../components/NivelBadge";
import Paginador from "../components/Paginador";

const inicial = { titulo: "", descripcion: "", nivel: "MEDIO", zonaId: "", enfermedadId: "" };

export default function Alertas() {
  const [alertasPage, setAlertasPage] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [zonas, setZonas] = useState([]);
  const [enfermedades, setEnfermedades] = useState([]);
  const [soloPendientes, setSoloPendientes] = useState(true);
  const [desde, setDesde] = useState("");
  const [hasta, setHasta] = useState("");
  const [form, setForm] = useState(inicial);
  const [guardando, setGuardando] = useState(false);
  const [exportando, setExportando] = useState(false);

  const cargar = () => {
    const desdeIso = desde ? `${desde}T00:00:00` : null;
    const hastaIso = hasta ? `${hasta}T23:59:59` : null;
    getAlertas(soloPendientes, desdeIso, hastaIso, pagina).then(setAlertasPage).catch(() => {});
    getZonas().then(setZonas).catch(() => {});
    getEnfermedades().then(setEnfermedades).catch(() => {});
  };

  const alertas = alertasPage?.content ?? [];

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [soloPendientes, desde, hasta, pagina]);

  // Los filtros siempre reinician la paginacion a la primera pagina
  const cambiarFiltro = (setter) => (valor) => {
    setPagina(0);
    setter(valor);
  };

  const exportar = async () => {
    setExportando(true);
    try {
      const desdeIso = desde ? `${desde}T00:00:00` : null;
      const hastaIso = hasta ? `${hasta}T23:59:59` : null;
      await descargarReporteAlertas(desdeIso, hastaIso);
    } finally {
      setExportando(false);
    }
  };

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const guardar = async (e) => {
    e.preventDefault();
    setGuardando(true);
    try {
      await crearAlerta({
        ...form,
        zonaId: form.zonaId ? Number(form.zonaId) : null,
        enfermedadId: form.enfermedadId ? Number(form.enfermedadId) : null,
      });
      setForm(inicial);
      cargar();
    } finally {
      setGuardando(false);
    }
  };

  const atender = async (id) => {
    await atenderAlerta(id);
    cargar();
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: 24 }}>
        <h2><Siren size={18} color="var(--critico)" /> Registrar alerta manual</h2>
        <form onSubmit={guardar}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 12 }}>
            <div>
              <label>Título</label>
              <input required value={form.titulo} onChange={(e) => set("titulo", e.target.value)} />
            </div>
            <div>
              <label>Nivel</label>
              <select value={form.nivel} onChange={(e) => set("nivel", e.target.value)}>
                <option value="BAJO">Bajo</option>
                <option value="MEDIO">Medio</option>
                <option value="ALTO">Alto</option>
                <option value="CRITICO">Crítico</option>
              </select>
            </div>
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
              <label>Enfermedad asociada</label>
              <select value={form.enfermedadId} onChange={(e) => set("enfermedadId", e.target.value)}>
                <option value="">(ninguna)</option>
                {enfermedades.map((en) => (
                  <option key={en.id} value={en.id}>{en.nombre}</option>
                ))}
              </select>
            </div>
            <div style={{ gridColumn: "1 / -1" }}>
              <label>Descripción</label>
              <input value={form.descripcion} onChange={(e) => set("descripcion", e.target.value)} />
            </div>
          </div>
          <button className="primary" type="submit" disabled={guardando} style={{ marginTop: 12 }}>
            <TriangleAlert size={14} /> {guardando ? "Guardando..." : "Registrar alerta"}
          </button>
        </form>
      </div>

      <div className="card">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12, flexWrap: "wrap", gap: 12 }}>
          <h2 style={{ margin: 0 }}><TriangleAlert size={18} color="var(--danger)" /> Alertas ({alertasPage?.totalElements ?? 0})</h2>
          <div style={{ display: "flex", alignItems: "center", gap: 12, flexWrap: "wrap" }}>
            <label style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 13 }}>
              <input
                type="checkbox"
                style={{ width: "auto" }}
                checked={soloPendientes}
                onChange={(e) => cambiarFiltro(setSoloPendientes)(e.target.checked)}
              />
              Solo pendientes
            </label>
            <input type="date" value={desde} onChange={(e) => cambiarFiltro(setDesde)(e.target.value)} style={{ width: 150 }} title="Desde" />
            <span style={{ color: "var(--text-muted)", fontSize: 13 }}>a</span>
            <input type="date" value={hasta} onChange={(e) => cambiarFiltro(setHasta)(e.target.value)} style={{ width: 150 }} title="Hasta" />
            <button onClick={exportar} disabled={exportando}>
              <Download size={14} /> {exportando ? "Exportando..." : "Exportar CSV"}
            </button>
          </div>
        </div>

        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
              <th style={{ padding: "6px 8px" }}>Título</th>
              <th style={{ padding: "6px 8px" }}>Nivel</th>
              <th style={{ padding: "6px 8px" }}>Zona</th>
              <th style={{ padding: "6px 8px" }}>Fecha</th>
              <th style={{ padding: "6px 8px" }}>Estado</th>
              <th style={{ padding: "6px 8px" }}></th>
            </tr>
          </thead>
          <tbody>
            {alertas.map((a) => (
              <tr key={a.id} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>
                  {a.titulo}
                  {a.descripcion && (
                    <div style={{ fontSize: 12, color: "var(--text-muted)" }}>{a.descripcion}</div>
                  )}
                </td>
                <td style={{ padding: "6px 8px" }}><NivelBadge nivel={a.nivel} /></td>
                <td style={{ padding: "6px 8px" }}>{a.zona?.nombre ?? "-"}</td>
                <td style={{ padding: "6px 8px" }}>{new Date(a.fechaGeneracion).toLocaleString()}</td>
                <td style={{ padding: "6px 8px" }}>
                  {a.atendida ? (
                    <span style={{ display: "inline-flex", alignItems: "center", gap: 4, color: "var(--accent)", fontSize: 13 }}>
                      <CircleCheck size={14} /> Atendida
                    </span>
                  ) : (
                    <span style={{ color: "var(--text-muted)", fontSize: 13 }}>Pendiente</span>
                  )}
                </td>
                <td style={{ padding: "6px 8px" }}>
                  {!a.atendida && (
                    <button onClick={() => atender(a.id)}>
                      <CircleCheck size={14} /> Atender
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <Paginador page={alertasPage} onCambiar={setPagina} />
      </div>
    </div>
  );
}
