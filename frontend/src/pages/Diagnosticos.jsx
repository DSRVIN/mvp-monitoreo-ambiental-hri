import { useEffect, useState } from "react";
import { ClipboardPlus, ClipboardList } from "lucide-react";
import {
  getDiagnosticos,
  registrarDiagnostico,
  getPacientes,
  getEnfermedades,
  getZonas,
} from "../services/monitoreoService";
import Paginador from "../components/Paginador";

const hoy = () => new Date().toISOString().slice(0, 10);

const inicial = {
  pacienteId: "",
  enfermedadId: "",
  zonaId: "",
  fechaDiagnostico: hoy(),
};

export default function Diagnosticos() {
  const [diagnosticosPage, setDiagnosticosPage] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [pacientes, setPacientes] = useState([]);
  const [enfermedades, setEnfermedades] = useState([]);
  const [zonas, setZonas] = useState([]);
  const [form, setForm] = useState(inicial);
  const [guardando, setGuardando] = useState(false);
  const [mensaje, setMensaje] = useState(null);

  const diagnosticos = diagnosticosPage?.content ?? [];

  const cargar = () => {
    getDiagnosticos(pagina).then(setDiagnosticosPage).catch(() => {});
    // El formulario necesita el listado completo de pacientes, no solo una pagina
    getPacientes(0, 1000).then((p) => setPacientes(p.content)).catch(() => {});
    getEnfermedades().then(setEnfermedades).catch(() => {});
    getZonas().then(setZonas).catch(() => {});
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagina]);

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  // Al elegir un paciente, se sugiere automaticamente su zona de residencia
  const seleccionarPaciente = (pacienteId) => {
    const paciente = pacientes.find((p) => String(p.id) === pacienteId);
    setForm((f) => ({
      ...f,
      pacienteId,
      zonaId: paciente?.zona?.id ? String(paciente.zona.id) : f.zonaId,
    }));
  };

  const guardar = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setMensaje(null);
    try {
      await registrarDiagnostico({
        pacienteId: Number(form.pacienteId),
        enfermedadId: Number(form.enfermedadId),
        zonaId: form.zonaId ? Number(form.zonaId) : null,
        fechaDiagnostico: form.fechaDiagnostico,
      });
      setForm(inicial);
      setMensaje("Diagnóstico registrado. Se incluirá en el próximo análisis de zonas críticas.");
      cargar();
    } catch {
      setMensaje("Error al registrar el diagnóstico.");
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: 24 }}>
        <h2><ClipboardPlus size={18} color="var(--agua)" /> Registrar diagnóstico</h2>
        <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "0 0 12px" }}>
          Vincula un paciente con una enfermedad y una zona en una fecha determinada. Estos
          registros alimentan el Servicio de Análisis (correlación por zona / RF06).
        </p>
        <form onSubmit={guardar}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 12 }}>
            <div>
              <label>Paciente</label>
              <select required value={form.pacienteId} onChange={(e) => seleccionarPaciente(e.target.value)}>
                <option value="">Seleccionar...</option>
                {pacientes.map((p) => (
                  <option key={p.id} value={p.id}>{p.nombres} {p.apellidos} ({p.documento})</option>
                ))}
              </select>
            </div>
            <div>
              <label>Enfermedad</label>
              <select required value={form.enfermedadId} onChange={(e) => set("enfermedadId", e.target.value)}>
                <option value="">Seleccionar...</option>
                {enfermedades.map((en) => (
                  <option key={en.id} value={en.id}>{en.nombre}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Zona</label>
              <select value={form.zonaId} onChange={(e) => set("zonaId", e.target.value)}>
                <option value="">(usar zona del paciente)</option>
                {zonas.map((z) => (
                  <option key={z.id} value={z.id}>{z.nombre}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Fecha de diagnóstico</label>
              <input
                type="date"
                required
                value={form.fechaDiagnostico}
                onChange={(e) => set("fechaDiagnostico", e.target.value)}
              />
            </div>
          </div>

          {mensaje && <p style={{ fontSize: 13, color: "var(--text-muted)" }}>{mensaje}</p>}

          <button className="primary" type="submit" disabled={guardando} style={{ marginTop: 12 }}>
            <ClipboardPlus size={14} /> {guardando ? "Guardando..." : "Registrar diagnóstico"}
          </button>
        </form>
      </div>

      <div className="card">
        <h2><ClipboardList size={18} color="var(--agua)" /> Diagnósticos registrados ({diagnosticosPage?.totalElements ?? 0})</h2>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
              <th style={{ padding: "6px 8px" }}>Paciente</th>
              <th style={{ padding: "6px 8px" }}>Enfermedad</th>
              <th style={{ padding: "6px 8px" }}>Zona</th>
              <th style={{ padding: "6px 8px" }}>Fecha</th>
            </tr>
          </thead>
          <tbody>
            {diagnosticos.map((d) => (
              <tr key={d.id} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>{d.paciente?.nombres} {d.paciente?.apellidos}</td>
                <td style={{ padding: "6px 8px" }}>{d.enfermedad?.nombre}</td>
                <td style={{ padding: "6px 8px" }}>{d.zona?.nombre ?? "-"}</td>
                <td style={{ padding: "6px 8px" }}>{d.fechaDiagnostico}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <Paginador page={diagnosticosPage} onCambiar={setPagina} />
      </div>
    </div>
  );
}
