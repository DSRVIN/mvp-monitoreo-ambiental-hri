import { useEffect, useState } from "react";
import { UserCog, UserPlus, Trash2, Power } from "lucide-react";
import {
  getUsuarios,
  register,
  cambiarEstadoUsuario,
  eliminarUsuario,
} from "../services/monitoreoService";

const ROLES = [
  { value: "MEDICO", label: "Médico" },
  { value: "SUPERVISOR", label: "Supervisor" },
  { value: "ADMIN", label: "Administrador" },
];

const inicial = { nombre: "", email: "", password: "", rol: "MEDICO" };

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [form, setForm] = useState(inicial);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState(null);

  const cargar = () => getUsuarios().then(setUsuarios).catch(() => {});
  useEffect(() => {
    cargar();
  }, []);

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const crear = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setError(null);
    try {
      await register(form);
      setForm(inicial);
      cargar();
    } catch {
      setError("No se pudo crear el usuario. Verifica que el correo no esté repetido.");
    } finally {
      setGuardando(false);
    }
  };

  const alternarEstado = async (u) => {
    await cambiarEstadoUsuario(u.id, !u.activo);
    cargar();
  };

  const borrar = async (u) => {
    if (!window.confirm(`¿Eliminar al usuario ${u.email}?`)) return;
    await eliminarUsuario(u.id);
    cargar();
  };

  return (
    <div>
      <div className="card" style={{ marginBottom: 24 }}>
        <h2><UserPlus size={18} color="var(--accent)" /> Crear usuario</h2>
        <form onSubmit={crear}>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: 12 }}>
            <div>
              <label>Nombre</label>
              <input required value={form.nombre} onChange={(e) => set("nombre", e.target.value)} />
            </div>
            <div>
              <label>Correo</label>
              <input type="email" required value={form.email} onChange={(e) => set("email", e.target.value)} />
            </div>
            <div>
              <label>Contraseña</label>
              <input type="password" required minLength={6} value={form.password} onChange={(e) => set("password", e.target.value)} />
            </div>
            <div>
              <label>Rol</label>
              <select value={form.rol} onChange={(e) => set("rol", e.target.value)}>
                {ROLES.map((r) => (
                  <option key={r.value} value={r.value}>{r.label}</option>
                ))}
              </select>
            </div>
          </div>
          {error && <p style={{ color: "var(--danger)", fontSize: 13 }}>{error}</p>}
          <button className="primary" type="submit" disabled={guardando} style={{ marginTop: 12 }}>
            <UserPlus size={14} /> {guardando ? "Creando..." : "Crear usuario"}
          </button>
        </form>
      </div>

      <div className="card">
        <h2><UserCog size={18} color="var(--accent)" /> Usuarios del sistema ({usuarios.length})</h2>
        <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
          <thead>
            <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
              <th style={{ padding: "6px 8px" }}>Nombre</th>
              <th style={{ padding: "6px 8px" }}>Correo</th>
              <th style={{ padding: "6px 8px" }}>Rol</th>
              <th style={{ padding: "6px 8px" }}>Estado</th>
              <th style={{ padding: "6px 8px" }}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {usuarios.map((u) => (
              <tr key={u.id} style={{ borderTop: "1px solid var(--border)" }}>
                <td style={{ padding: "6px 8px" }}>{u.nombre}</td>
                <td style={{ padding: "6px 8px" }}>{u.email}</td>
                <td style={{ padding: "6px 8px" }}>{u.rol}</td>
                <td style={{ padding: "6px 8px" }}>
                  <span style={{ color: u.activo ? "var(--accent)" : "var(--text-muted)", fontSize: 13 }}>
                    {u.activo ? "Activo" : "Inactivo"}
                  </span>
                </td>
                <td style={{ padding: "6px 8px" }}>
                  <div style={{ display: "flex", gap: 6 }}>
                    <button onClick={() => alternarEstado(u)} title={u.activo ? "Desactivar" : "Activar"}>
                      <Power size={14} /> {u.activo ? "Desactivar" : "Activar"}
                    </button>
                    <button onClick={() => borrar(u)} title="Eliminar" style={{ color: "var(--danger)" }}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
