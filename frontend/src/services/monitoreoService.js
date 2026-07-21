import api from "../api/axios";

// --- Autenticacion ---
export const login = (email, password) =>
  api.post("/auth/login", { email, password }).then((r) => r.data);

export const register = (data) =>
  api.post("/auth/register", data).then((r) => r.data);

// --- Pacientes (paginado) ---
export const getPacientes = (page = 0, size = 10) =>
  api.get("/pacientes", { params: { page, size } }).then((r) => r.data);

export const crearPaciente = (data) =>
  api.post("/pacientes", data).then((r) => r.data);

export const actualizarPaciente = (id, data) =>
  api.put(`/pacientes/${id}`, data).then((r) => r.data);

export const eliminarPaciente = (id) =>
  api.delete(`/pacientes/${id}`).then((r) => r.data);

// --- Enfermedades (CRUD) ---
export const getEnfermedades = () => api.get("/enfermedades").then((r) => r.data);

export const crearEnfermedad = (data) =>
  api.post("/enfermedades", data).then((r) => r.data);

export const actualizarEnfermedad = (id, data) =>
  api.put(`/enfermedades/${id}`, data).then((r) => r.data);

export const eliminarEnfermedad = (id) =>
  api.delete(`/enfermedades/${id}`).then((r) => r.data);

// --- Zonas (CRUD) ---
export const getZonas = () => api.get("/zonas").then((r) => r.data);

export const crearZona = (data) =>
  api.post("/zonas", data).then((r) => r.data);

export const actualizarZona = (id, data) =>
  api.put(`/zonas/${id}`, data).then((r) => r.data);

export const eliminarZona = (id) =>
  api.delete(`/zonas/${id}`).then((r) => r.data);

// --- Datos ambientales (CRUD) ---
export const getDatosAmbientales = () =>
  api.get("/ambiental").then((r) => r.data);

export const crearDatoAmbiental = (data) =>
  api.post("/ambiental", data).then((r) => r.data);

export const actualizarDatoAmbiental = (id, data) =>
  api.put(`/ambiental/${id}`, data).then((r) => r.data);

export const eliminarDatoAmbiental = (id) =>
  api.delete(`/ambiental/${id}`).then((r) => r.data);

// --- Usuarios (gestion, solo ADMIN) ---
export const getUsuarios = () => api.get("/usuarios").then((r) => r.data);

export const cambiarEstadoUsuario = (id, activo) =>
  api.patch(`/usuarios/${id}/estado`, null, { params: { activo } }).then((r) => r.data);

export const eliminarUsuario = (id) =>
  api.delete(`/usuarios/${id}`).then((r) => r.data);

// --- Alertas (paginado) ---
export const getAlertas = (soloPendientes = false, desde = null, hasta = null, page = 0, size = 10) =>
  api.get("/alertas", { params: { soloPendientes, desde, hasta, page, size } }).then((r) => r.data);

export const crearAlerta = (data) =>
  api.post("/alertas", data).then((r) => r.data);

export const atenderAlerta = (id) =>
  api.patch(`/alertas/${id}/atender`).then((r) => r.data);

export const eliminarAlerta = (id) =>
  api.delete(`/alertas/${id}`).then((r) => r.data);

// --- Analisis epidemiologico (Servicio de Analisis) ---
export const getZonasCriticas = () =>
  api.get("/analisis/zonas-criticas").then((r) => r.data);

export const ejecutarAnalisis = () =>
  api.post("/analisis/ejecutar").then((r) => r.data);

// --- Diagnosticos (vincula paciente + enfermedad + zona, paginado) ---
export const getDiagnosticos = (page = 0, size = 10) =>
  api.get("/diagnosticos", { params: { page, size } }).then((r) => r.data);

export const registrarDiagnostico = (data) =>
  api.post("/diagnosticos", data).then((r) => r.data);

// --- Integraciones externas (SENAMHI, ANA, Municipalidad) ---
export const consultarSenamhi = (zonaId) =>
  api.get("/integraciones/senamhi/mock", { params: { zonaId } }).then((r) => r.data);

export const importarSenamhi = (zonaId) =>
  api.post("/integraciones/senamhi/importar", null, { params: { zonaId } }).then((r) => r.data);

export const importarCsvAna = (archivo) => {
  const formData = new FormData();
  formData.append("archivo", archivo);
  return api
    .post("/integraciones/ana/csv", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    })
    .then((r) => r.data);
};

export const getCalidadAgua = () =>
  api.get("/integraciones/ana/calidad-agua").then((r) => r.data);

export const registrarReporteResiduos = (data) =>
  api.post("/integraciones/municipalidad/reportes-residuos", data).then((r) => r.data);

export const getReportesResiduos = () =>
  api.get("/integraciones/municipalidad/reportes-residuos").then((r) => r.data);

// --- Reportes estadisticos exportables (RF11) ---
function descargarBlob(blob, nombreArchivo) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", nombreArchivo);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

export const descargarReporteZonasCriticas = () =>
  api
    .get("/reportes/zonas-criticas/csv", { responseType: "blob" })
    .then((r) => descargarBlob(r.data, "reporte-zonas-criticas.csv"));

export const descargarReporteAlertas = (desde = null, hasta = null) =>
  api
    .get("/reportes/alertas/csv", { params: { desde, hasta }, responseType: "blob" })
    .then((r) => descargarBlob(r.data, "reporte-alertas.csv"));

// --- Simulacion de datos ---
export const simularDatos = (params) =>
  api.post("/simulacion/datos-ambientales", params).then((r) => r.data);

export const limpiarSimulados = () =>
  api.delete("/simulacion/datos-ambientales").then((r) => r.data);
