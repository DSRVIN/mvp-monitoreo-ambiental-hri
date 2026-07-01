import { NavLink, Outlet } from "react-router-dom";
import { Leaf, LayoutDashboard, Users, Stethoscope, TriangleAlert, LogOut, Radio } from "lucide-react";
import { useAuth } from "../context/AuthContext";

const enlaces = [
  { to: "/", label: "Dashboard", end: true, icon: LayoutDashboard },
  { to: "/pacientes", label: "Pacientes", icon: Users },
  { to: "/diagnosticos", label: "Diagnósticos", icon: Stethoscope },
  { to: "/alertas", label: "Alertas", icon: TriangleAlert },
  { to: "/integraciones", label: "Integraciones", icon: Radio },
];

export default function Layout() {
  const { usuario, logout } = useAuth();

  return (
    <div style={{ maxWidth: 1280, margin: "0 auto", padding: 24 }}>
      <header
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          gap: 16,
          marginBottom: 16,
        }}
      >
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div
            style={{
              width: 40,
              height: 40,
              borderRadius: 10,
              background: "var(--accent-soft)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "var(--accent)",
            }}
          >
            <Leaf size={22} />
          </div>
          <div>
            <h1>Monitoreo Ambiental</h1>
            <div style={{ fontSize: 13, color: "var(--text-muted)" }}>
              Hospital Regional de Ica
            </div>
          </div>
        </div>

        <div style={{ textAlign: "right", whiteSpace: "nowrap" }}>
          <div style={{ fontSize: 13, color: "var(--text-muted)", marginBottom: 6 }}>
            {usuario?.email} ({usuario?.rol})
          </div>
          <button onClick={logout}>
            <LogOut size={14} /> Salir
          </button>
        </div>
      </header>

      <nav
        style={{
          display: "flex",
          gap: 4,
          marginBottom: 24,
          borderBottom: "1px solid var(--border)",
          paddingBottom: 12,
        }}
      >
        {enlaces.map((e) => {
          const Icono = e.icon;
          return (
            <NavLink
              key={e.to}
              to={e.to}
              end={e.end}
              style={({ isActive }) => ({
                display: "inline-flex",
                alignItems: "center",
                gap: 6,
                padding: "8px 14px",
                borderRadius: 8,
                textDecoration: "none",
                fontSize: 14,
                fontWeight: 500,
                color: isActive ? "#fff" : "var(--text-muted)",
                background: isActive ? "var(--accent)" : "transparent",
              })}
            >
              <Icono size={16} />
              {e.label}
            </NavLink>
          );
        })}
      </nav>

      <Outlet />
    </div>
  );
}
