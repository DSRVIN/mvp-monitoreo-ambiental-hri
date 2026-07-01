import { useEffect, useState } from "react";
import { UserPlus, Users } from "lucide-react";
import { getPacientes, crearPaciente, getZonas } from "../services/monitoreoService";
import Paginador from "../components/Paginador";

const inicial = {
  nombres: "",
  apellidos: "",
  documento: "",
  fechaNacimiento: "",
  sexo: "M",
  direccion: "",
  telefono: "",
  zonaId: "",
};

export default function Pacientes() {
  const [pacientesPage, setPacientesPage] = useState(null);
  const [pagina, setPagina] = useState(0);
  const [zonas, setZonas] = useState([]);
  const [form, setForm] = useState(inicial);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState(null);

  const cargar = () => {
    getPacientes(pagina).then(setPacientesPage).catch(() => {});
    getZonas().then(setZonas).catch(() => {});
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pagina]);

  const pacientes = pacientesPage?.content ?? [];

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const guardar = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setError(null);
    try {
      await crearPaciente({
        ...form,
        zonaId: form.zonaId ? Number(form.zonaId) : null,
        fechaNacimiento: form.fechaNacimiento || null,
      });
      setForm(inicial);
      cargar();
    } catch {
      setError("No se pudo registrar el paciente. Verifica que el documento no esté repetido.");
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: 24 }}>
        <h2><UserPlus size={18} color="var(--accent)" /> Registrar paciente</h2>
        <form onSubmit={guardar}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 12 }}>
            <div>
              <label>Nombres</label>
              <input required value={form.nombres} onChange={(e) => set("nombres", e.target.value)} />
            </div>
            <div>
              <label>Apellidos</label>
              <input required value={form.apellidos} onChange={(e) => set("apellidos", e.target.value)} />
            </div>
            <div>
              <label>Documento</label>
              <input required value={form.documento} onChange={(e) => set("documento", e.target.value)} />
            </div>
            <div>
              <label>Fecha de nacimiento</label>
              <input type="date" value={form.fechaNacimiento} onChange={(e) => set("fechaNacimiento", e.target.value)} />
            </div>
            <div>
              <label>Sexo</label>
              <select value={form.sexo} onChange={(e) => set("sexo", e.target.value)}>
                <option value="M">Masculino</option>
                <option value="F">Femenino</option>
              </select>
            </div>
            <div>
              <label>Distrito / Zona</label>
              <select required value={form.zonaId} onChange={(e) => set("zonaId", e.target.value)}>
                <option value="">Seleccionar...</option>
                {zonas.map((z) => (
                  <option key={z.id} value={z.id}>{z.nombre}</option>
                ))}
              </select>
            </div>
            <div>
              <label>Dirección</label>
              <input value={form.direccion} onChange={(e) => set("direccion", e.target.value)} />
            </div>
            <div>
              <label>Teléfono</label>
              <input value={form.telefono} onChange={(e) => set("telefono", e.target.value)} />
            </div>
          </div>

          {error && <p style={{ color: "var(--danger)", fontSize: 13 }}>{error}</p>}

          <button className="primary" type="submit" disabled={guardando} style={{ marginTop: 12 }}>
            <UserPlus size={14} /> {guardando ? "Guardando..." : "Registrar paciente"}
          </button>
        </form>
      </div>

      <div className="card">
        <h2><Users size={18} color="var(--accent)" /> Pacientes registrados ({pacientesPage?.totalElements ?? 0})</h2>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
              <th style={{ padding: "6px 8px" }}>Nombre</th>
              <th style={{ padding: "6px 8px" }}>Documento</th>
              <th style={{ padding: "6px 8px" }}>Zona</th>
              <th style={{ padding: "6px 8px" }}>Teléfono</th>
            </tr>
          </thead>
          <tbody>
            {pacientes.map((p) => (
              <tr key={p.id} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>{p.nombres} {p.apellidos}</td>
                <td style={{ padding: "6px 8px" }}>{p.documento}</td>
                <td style={{ padding: "6px 8px" }}>{p.zona?.nombre ?? "-"}</td>
                <td style={{ padding: "6px 8px" }}>{p.telefono ?? "-"}</td>
              </tr>
            ))}
          </tbody>
        </table>
        <Paginador page={pacientesPage} onCambiar={setPagina} />
      </div>
    </div>
  );
}
