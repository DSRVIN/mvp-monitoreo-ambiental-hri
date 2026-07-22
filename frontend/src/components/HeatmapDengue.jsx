import { useEffect } from "react";
import { MapContainer, TileLayer, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet.heat";

// Capa de calor: agrega/quita una L.heatLayer sobre el mapa segun los puntos.
function CapaCalor({ puntos }) {
  const map = useMap();

  useEffect(() => {
    if (!puntos.length) return;
    const maxCasos = Math.max(...puntos.map((p) => p.casos));
    const datos = puntos.map((p) => [p.lat, p.lng, p.casos]);
    const capa = L.heatLayer(datos, {
      radius: 35,
      blur: 25,
      max: maxCasos,
      minOpacity: 0.35,
      gradient: { 0.2: "#2ecc71", 0.4: "#f1c40f", 0.6: "#e67e22", 0.8: "#e74c3c", 1.0: "#a80000" },
    }).addTo(map);
    return () => map.removeLayer(capa);
  }, [puntos, map]);

  return null;
}

const LEYENDA = [
  { c: "#2ecc71", t: "Bajo" }, { c: "#f1c40f", t: "Moderado" },
  { c: "#e67e22", t: "Alto" }, { c: "#e74c3c", t: "Muy alto" }, { c: "#a80000", t: "Crítico" },
];

// Mapa de calor reutilizable. Se usa en la pagina de Mapa de calor y en el Dashboard.
export default function HeatmapDengue({ puntos = [], height = 460, center = [-13.9, -75.75], zoom = 9, leyenda = true }) {
  return (
    <div>
      <MapContainer center={center} zoom={zoom} style={{ height, width: "100%", borderRadius: 10 }}>
        <TileLayer
          attribution='&copy; OpenStreetMap contributors &copy; CARTO'
          url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
        />
        <CapaCalor puntos={puntos} />
      </MapContainer>
      {leyenda && (
        <div style={{ display: "flex", gap: 16, flexWrap: "wrap", marginTop: 10, fontSize: 12, color: "var(--text-muted)" }}>
          <span>Intensidad = número de casos:</span>
          {LEYENDA.map((l) => (
            <span key={l.t} style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 12, height: 12, borderRadius: 3, background: l.c, display: "inline-block" }} />
              {l.t}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
