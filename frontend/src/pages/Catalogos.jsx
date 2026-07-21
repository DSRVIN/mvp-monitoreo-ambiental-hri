import { useEffect, useState } from "react";
import { Stethoscope, MapPin, Wind } from "lucide-react";
import GestorCrud from "../components/GestorCrud";
import NivelBadge from "../components/NivelBadge";
import {
  getEnfermedades, crearEnfermedad, actualizarEnfermedad, eliminarEnfermedad,
  getZonas, crearZona, actualizarZona, eliminarZona,
  getDatosAmbientales, crearDatoAmbiental, actualizarDatoAmbiental, eliminarDatoAmbiental,
} from "../services/monitoreoService";

// Convierte texto de formulario a numero (o null si esta vacio)
const num = (v) => (v === "" || v == null ? null : Number(v));

const NIVELES = [
  { value: "BAJO", label: "Bajo" },
  { value: "MEDIO", label: "Medio" },
  { value: "ALTO", label: "Alto" },
  { value: "CRITICO", label: "Crítico" },
];

export default function Catalogos() {
  const [zonas, setZonas] = useState([]);

  const cargarZonas = () => getZonas().then(setZonas).catch(() => {});
  useEffect(() => {
    cargarZonas();
  }, []);

  return (
    <div style={{ display: "grid", gap: 24 }}>
      <p style={{ color: "var(--text-muted)", fontSize: 13, margin: 0 }}>
        Gestión de los catálogos base del sistema: enfermedades, zonas geográficas y datos
        ambientales. Cada tabla permite crear, editar y eliminar registros.
      </p>

      {/* --- Enfermedades --- */}
      <GestorCrud
        titulo="Enfermedades"
        icono={Stethoscope}
        color="var(--agua)"
        campos={[
          { name: "nombre", label: "Nombre", required: true },
          { name: "codigoCie10", label: "Código CIE-10" },
          { name: "contaminanteAsociado", label: "Contaminante asociado" },
          { name: "descripcion", label: "Descripción", type: "textarea" },
        ]}
        columnas={[
          { header: "Nombre", render: (e) => e.nombre },
          { header: "CIE-10", render: (e) => e.codigoCie10 ?? "-" },
          { header: "Contaminante", render: (e) => e.contaminanteAsociado ?? "-" },
        ]}
        cargar={getEnfermedades}
        crear={crearEnfermedad}
        actualizar={actualizarEnfermedad}
        eliminar={eliminarEnfermedad}
        aPayload={(f) => ({
          nombre: f.nombre,
          codigoCie10: f.codigoCie10 || null,
          contaminanteAsociado: f.contaminanteAsociado || null,
          descripcion: f.descripcion || null,
        })}
        aFormulario={(e) => ({
          nombre: e.nombre ?? "",
          codigoCie10: e.codigoCie10 ?? "",
          contaminanteAsociado: e.contaminanteAsociado ?? "",
          descripcion: e.descripcion ?? "",
        })}
        nombreItem={(e) => `la enfermedad "${e.nombre}"`}
      />

      {/* --- Zonas --- */}
      <GestorCrud
        titulo="Zonas geográficas"
        icono={MapPin}
        color="var(--accent)"
        campos={[
          { name: "nombre", label: "Nombre del distrito", required: true },
          { name: "latitud", label: "Latitud", type: "number", step: "any", required: true },
          { name: "longitud", label: "Longitud", type: "number", step: "any", required: true },
          { name: "nivelRiesgo", label: "Nivel de riesgo", type: "select", options: NIVELES },
        ]}
        columnas={[
          { header: "Nombre", render: (z) => z.nombre },
          { header: "Latitud", render: (z) => z.latitud },
          { header: "Longitud", render: (z) => z.longitud },
          { header: "Riesgo", render: (z) => <NivelBadge nivel={z.nivelRiesgo} /> },
        ]}
        cargar={getZonas}
        crear={crearZona}
        actualizar={actualizarZona}
        eliminar={eliminarZona}
        onCambio={cargarZonas}
        aPayload={(f) => ({
          nombre: f.nombre,
          latitud: num(f.latitud),
          longitud: num(f.longitud),
          nivelRiesgo: f.nivelRiesgo || "BAJO",
        })}
        aFormulario={(z) => ({
          nombre: z.nombre ?? "",
          latitud: z.latitud ?? "",
          longitud: z.longitud ?? "",
          nivelRiesgo: z.nivelRiesgo ?? "BAJO",
        })}
        nombreItem={(z) => `la zona "${z.nombre}"`}
      />

      {/* --- Datos ambientales --- */}
      <GestorCrud
        titulo="Datos ambientales"
        icono={Wind}
        color="var(--tierra)"
        campos={[
          { name: "zonaId", label: "Zona", type: "select", required: true,
            options: [{ value: "", label: "Seleccionar..." }, ...zonas.map((z) => ({ value: String(z.id), label: z.nombre }))] },
          { name: "fuente", label: "Fuente" },
          { name: "indiceCalidadAire", label: "Índice ICA", type: "number" },
          { name: "pm25", label: "PM2.5", type: "number", step: "any" },
          { name: "pm10", label: "PM10", type: "number", step: "any" },
          { name: "o3", label: "O3", type: "number", step: "any" },
          { name: "no2", label: "NO2", type: "number", step: "any" },
        ]}
        columnas={[
          { header: "Zona", render: (d) => d.zona?.nombre ?? "-" },
          { header: "ICA", render: (d) => d.indiceCalidadAire ?? "-" },
          { header: "PM2.5", render: (d) => d.pm25 ?? "-" },
          { header: "PM10", render: (d) => d.pm10 ?? "-" },
          { header: "Fuente", render: (d) => d.fuente ?? "-" },
          { header: "Fecha", render: (d) => (d.fechaMedicion ? new Date(d.fechaMedicion).toLocaleDateString() : "-") },
        ]}
        cargar={getDatosAmbientales}
        crear={crearDatoAmbiental}
        actualizar={actualizarDatoAmbiental}
        eliminar={eliminarDatoAmbiental}
        aPayload={(f) => ({
          zonaId: f.zonaId ? Number(f.zonaId) : null,
          fuente: f.fuente || "Registro manual",
          indiceCalidadAire: num(f.indiceCalidadAire),
          pm25: num(f.pm25),
          pm10: num(f.pm10),
          o3: num(f.o3),
          no2: num(f.no2),
        })}
        aFormulario={(d) => ({
          zonaId: d.zona?.id ? String(d.zona.id) : "",
          fuente: d.fuente ?? "",
          indiceCalidadAire: d.indiceCalidadAire ?? "",
          pm25: d.pm25 ?? "",
          pm10: d.pm10 ?? "",
          o3: d.o3 ?? "",
          no2: d.no2 ?? "",
        })}
        nombreItem={(d) => `la medición #${d.id}`}
      />
    </div>
  );
}
