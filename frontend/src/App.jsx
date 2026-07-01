import { Routes, Route, Navigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import Layout from "./components/Layout";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import Pacientes from "./pages/Pacientes";
import Diagnosticos from "./pages/Diagnosticos";
import Alertas from "./pages/Alertas";
import Integraciones from "./pages/Integraciones";

function RutaProtegida({ children }) {
  const { isAuth } = useAuth();
  return isAuth ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route
        path="/"
        element={
          <RutaProtegida>
            <Layout />
          </RutaProtegida>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="pacientes" element={<Pacientes />} />
        <Route path="diagnosticos" element={<Diagnosticos />} />
        <Route path="alertas" element={<Alertas />} />
        <Route path="integraciones" element={<Integraciones />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
