import { createContext, useContext, useEffect, useState } from "react";
import { login as loginRequest } from "../services/monitoreoService";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [usuario, setUsuario] = useState(() => {
    const raw = localStorage.getItem("usuario");
    return raw ? JSON.parse(raw) : null;
  });

  useEffect(() => {
    if (token) localStorage.setItem("token", token);
    else localStorage.removeItem("token");
  }, [token]);

  const login = async (email, password) => {
    const data = await loginRequest(email, password);
    setToken(data.token);
    const user = { email: data.email, rol: data.rol };
    setUsuario(user);
    localStorage.setItem("usuario", JSON.stringify(user));
    return data;
  };

  const logout = () => {
    setToken(null);
    setUsuario(null);
    localStorage.removeItem("usuario");
  };

  return (
    <AuthContext.Provider value={{ token, usuario, login, logout, isAuth: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);
