import { useCallback, useEffect, useState } from "react";
import { Users, Stethoscope, Wind, TriangleAlert, Map, BarChart3 } from "lucide-react";
import {
  getDatosAmbientales,
  getAlertas,
  getPacientes,
  getEnfermedades,
} from "../services/monitoreoService";
import MapaContaminacion from "../components/MapaContaminacion";
import GraficoEstadisticas from "../components/GraficoEstadisticas";
import SimuladorPanel from "../components/SimuladorPanel";
import ZonasCriticasPanel from "../components/ZonasCriticasPanel";

export default function Dashboard() {
  const [datos, setDatos] = useState([]);
  const [resumen, setResumen] = useState({ pacientes: 0, enfermedades: 0, alertas: 0 });

  const cargar = useCallback(() => {
    getDatosAmbientales().then(setDatos).catch(() => {});
    Promise.all([getPacientes(0, 1), getEnfermedades(), getAlertas(true, null, null, 0, 1)])
      .then(([p, e, a]) =>
        setResumen({ pacientes: p.totalElements, enfermedades: e.length, alertas: a.totalElements })
      )
      .catch(() => {});
  }, []);

  useEffect(() => {
    cargar();
  }, [cargar]);

  return (
    <div>
      <section style={{ display: "flex", gap: 16, marginBottom: 24 }}>
        <Tarjeta titulo="Pacientes" valor={resumen.pacientes} icono={Users} color="var(--accent)" fondo="var(--accent-soft)" />
        <Tarjeta titulo="Enfermedades" valor={resumen.enfermedades} icono={Stethoscope} color="var(--agua)" fondo="var(--agua-soft)" />
        <Tarjeta titulo="Datos ambientales" valor={datos.length} icono={Wind} color="var(--tierra)" fondo="var(--tierra-soft)" />
        <Tarjeta titulo="Alertas pendientes" valor={resumen.alertas} icono={TriangleAlert} color="var(--danger)" fondo="#fbe6e6" />
      </section>

      <section style={{ marginBottom: 24 }}>
        <SimuladorPanel onCambio={cargar} />
      </section>

      <section style={{ marginBottom: 24 }}>
        <ZonasCriticasPanel onCambio={cargar} />
      </section>

      <section style={{ marginBottom: 24 }} className="card">
        <h2><Map size={18} color="var(--agua)" /> Mapa de contaminación</h2>
        <MapaContaminacion datos={datos} />
      </section>

      <section className="card">
        <h2><BarChart3 size={18} color="var(--accent)" /> Estadísticas</h2>
        <GraficoEstadisticas datos={datos} />
      </section>
    </div>
  );
}

function Tarjeta({ titulo, valor, icono: Icono, color, fondo }) {
  return (
    <div className="card" style={{ flex: 1, display: "flex", alignItems: "center", gap: 14 }}>
      <div
        style={{
          width: 44,
          height: 44,
          borderRadius: 10,
          background: fondo,
          color,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          flexShrink: 0,
        }}
      >
        <Icono size={22} />
      </div>
      <div>
        <h3 style={{ fontSize: 13, color: "var(--text-muted)", fontWeight: 500 }}>{titulo}</h3>
        <p style={{ fontSize: 28, fontWeight: 700, margin: 0, color: "var(--text)" }}>{valor}</p>
      </div>
    </div>
  );
}
