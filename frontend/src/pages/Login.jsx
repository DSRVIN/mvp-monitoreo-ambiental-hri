import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Leaf, LogIn, TriangleAlert } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("admin@hri.com");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      await login(email, password);
      navigate("/");
    } catch (err) {
      console.error("Error de login:", err);
      if (err.response) {
        setError(`Error ${err.response.status}: credenciales inválidas`);
      } else {
        setError(`No se pudo conectar con el servidor (${err.message}). ¿Backend en :8080?`);
      }
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "linear-gradient(160deg, var(--accent-soft) 0%, var(--agua-soft) 100%)",
      }}
    >
      <div className="card" style={{ maxWidth: 380, width: "100%", margin: 16 }}>
        <div style={{ textAlign: "center", marginBottom: 20 }}>
          <div
            style={{
              width: 56,
              height: 56,
              borderRadius: 14,
              background: "var(--accent-soft)",
              color: "var(--accent)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              margin: "0 auto 12px",
            }}
          >
            <Leaf size={28} />
          </div>
          <h1 style={{ fontSize: 20 }}>Monitoreo Ambiental</h1>
          <p style={{ color: "var(--text-muted)", fontSize: 13, margin: "4px 0 0" }}>
            Hospital Regional de Ica
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: 12 }}>
            <label>Correo electrónico</label>
            <input
              type="email"
              placeholder="usuario@hri.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          <div style={{ marginBottom: 12 }}>
            <label>Contraseña</label>
            <input
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && (
            <p style={{ color: "var(--danger)", fontSize: 13, display: "flex", alignItems: "center", gap: 6 }}>
              <TriangleAlert size={14} /> {error}
            </p>
          )}

          <button className="primary" type="submit" style={{ width: "100%", justifyContent: "center", marginTop: 8 }}>
            <LogIn size={16} /> Ingresar
          </button>
        </form>
      </div>
    </div>
  );
}
