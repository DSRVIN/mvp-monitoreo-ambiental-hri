import { useEffect, useState } from "react";
import { Plus, Pencil, Trash2, X, Save } from "lucide-react";

// Componente reutilizable de gestion CRUD (formulario + tabla con editar/eliminar).
// Se configura mediante props para cada entidad (enfermedades, zonas, datos ambientales...).
//
// Props:
//  - titulo, icono, color
//  - campos: [{ name, label, type: "text"|"number"|"select"|"textarea", options?, required?, step? }]
//  - columnas: [{ header, render: (item) => valor }]
//  - cargar: () => Promise<array>
//  - crear: (payload) => Promise
//  - actualizar: (id, payload) => Promise
//  - eliminar: (id) => Promise
//  - aPayload: (form) => objeto que se envia al backend
//  - aFormulario: (item) => objeto para rellenar el formulario al editar
//  - nombreItem: (item) => texto para el mensaje de confirmacion de borrado
export default function GestorCrud({
  titulo, icono: Icono, color = "var(--accent)",
  campos, columnas, cargar, crear, actualizar, eliminar,
  aPayload, aFormulario, nombreItem, onCambio,
}) {
  const vacio = Object.fromEntries(campos.map((c) => [c.name, c.type === "select" ? (c.options[0]?.value ?? "") : ""]));

  const [items, setItems] = useState([]);
  const [form, setForm] = useState(vacio);
  const [editandoId, setEditandoId] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState(null);

  const recargar = () => {
    Promise.resolve(cargar())
      .then((data) => setItems(Array.isArray(data) ? data : data?.content ?? []))
      .catch(() => {});
  };

  useEffect(() => {
    recargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const set = (campo, valor) => setForm((f) => ({ ...f, [campo]: valor }));

  const cancelar = () => {
    setEditandoId(null);
    setForm(vacio);
    setError(null);
  };

  const editar = (item) => {
    setEditandoId(item.id);
    setForm({ ...vacio, ...aFormulario(item) });
    setError(null);
  };

  const guardar = async (e) => {
    e.preventDefault();
    setGuardando(true);
    setError(null);
    try {
      const payload = aPayload(form);
      if (editandoId) await actualizar(editandoId, payload);
      else await crear(payload);
      cancelar();
      recargar();
      onCambio?.();
    } catch {
      setError("No se pudo guardar. Revisa los datos ingresados.");
    } finally {
      setGuardando(false);
    }
  };

  const borrar = async (item) => {
    if (!window.confirm(`¿Eliminar ${nombreItem(item)}?`)) return;
    try {
      await eliminar(item.id);
      if (editandoId === item.id) cancelar();
      recargar();
      onCambio?.();
    } catch {
      setError("No se pudo eliminar (puede estar en uso por otros registros).");
    }
  };

  return (
    <div className="card">
      <h2>{Icono && <Icono size={18} color={color} />} {titulo} ({items.length})</h2>

      <form onSubmit={guardar} style={{ marginBottom: 16 }}>
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))", gap: 12 }}>
          {campos.map((c) => (
            <div key={c.name} style={c.type === "textarea" ? { gridColumn: "1 / -1" } : undefined}>
              <label>{c.label}</label>
              {c.type === "select" ? (
                <select required={c.required} value={form[c.name]} onChange={(e) => set(c.name, e.target.value)}>
                  {c.options.map((o) => (
                    <option key={o.value} value={o.value}>{o.label}</option>
                  ))}
                </select>
              ) : (
                <input
                  type={c.type === "number" ? "number" : c.type === "date" ? "date" : "text"}
                  step={c.step}
                  required={c.required}
                  value={form[c.name]}
                  onChange={(e) => set(c.name, e.target.value)}
                />
              )}
            </div>
          ))}
        </div>

        {error && <p style={{ color: "var(--danger)", fontSize: 13 }}>{error}</p>}

        <div style={{ display: "flex", gap: 10, marginTop: 12 }}>
          <button className="primary" type="submit" disabled={guardando}>
            {editandoId ? <Save size={14} /> : <Plus size={14} />}
            {guardando ? "Guardando..." : editandoId ? "Actualizar" : "Agregar"}
          </button>
          {editandoId && (
            <button type="button" onClick={cancelar}><X size={14} /> Cancelar</button>
          )}
        </div>
      </form>

      <table style={{ width: "100%", borderCollapse: "collapse", fontSize: 14 }}>
        <thead>
          <tr style={{ textAlign: "left", color: "var(--text-muted)" }}>
            {columnas.map((col) => (
              <th key={col.header} style={{ padding: "6px 8px" }}>{col.header}</th>
            ))}
            <th style={{ padding: "6px 8px" }}>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id} style={{ borderTop: "1px solid var(--border)" }}>
              {columnas.map((col) => (
                <td key={col.header} style={{ padding: "6px 8px" }}>{col.render(item)}</td>
              ))}
              <td style={{ padding: "6px 8px" }}>
                <div style={{ display: "flex", gap: 6 }}>
                  <button onClick={() => editar(item)} title="Editar"><Pencil size={14} /></button>
                  <button onClick={() => borrar(item)} title="Eliminar" style={{ color: "var(--danger)" }}><Trash2 size={14} /></button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
