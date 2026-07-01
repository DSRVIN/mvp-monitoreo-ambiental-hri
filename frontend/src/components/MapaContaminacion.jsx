import { MapContainer, TileLayer, CircleMarker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";

// Colorea cada punto segun el indice de calidad del aire (ICA), usando la paleta ambiental
function colorPorIca(ica) {
  if (ica == null) return "#888";
  if (ica <= 50) return "var(--bajo)";
  if (ica <= 100) return "var(--medio)";
  if (ica <= 150) return "var(--alto)";
  return "var(--critico)";
}

// El radio del marcador crece con el impacto (ICA)
function radioPorIca(ica) {
  if (ica == null) return 6;
  return Math.min(28, 6 + ica / 12);
}

const LEYENDA = [
  { label: "Bueno (0–50)", color: "var(--bajo)" },
  { label: "Moderado (51–100)", color: "var(--medio)" },
  { label: "Malo (101–150)", color: "var(--alto)" },
  { label: "Crítico (>150)", color: "var(--critico)" },
];

// Centro por defecto: ciudad de Ica (region del Hospital Regional de Ica)
export default function MapaContaminacion({ datos = [], center = [-14.0678, -75.7286] }) {
  return (
    <div>
      <MapContainer center={center} zoom={11} style={{ height: "400px", width: "100%", borderRadius: 10 }}>
        <TileLayer
          attribution='&copy; OpenStreetMap contributors &copy; CARTO'
          url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
        />
        {datos
          .filter((d) => d.latitud != null && d.longitud != null)
          .map((d) => (
            <CircleMarker
              key={d.id}
              center={[d.latitud, d.longitud]}
              radius={radioPorIca(d.indiceCalidadAire)}
              pathOptions={{ color: colorPorIca(d.indiceCalidadAire), fillColor: colorPorIca(d.indiceCalidadAire), fillOpacity: 0.55, weight: 2 }}
            >
              <Popup>
                <strong>Índice de Calidad del Aire: {d.indiceCalidadAire ?? "N/D"}</strong>
                <br />
                PM2.5: {d.pm25 ?? "-"} µg/m³ | PM10: {d.pm10 ?? "-"} µg/m³
                <br />
                Fuente: {d.fuente ?? "-"}
              </Popup>
            </CircleMarker>
          ))}
      </MapContainer>

      <div style={{ display: "flex", gap: 16, flexWrap: "wrap", marginTop: 10, fontSize: 12, color: "var(--text-muted)" }}>
        {LEYENDA.map((l) => (
          <span key={l.label} style={{ display: "inline-flex", alignItems: "center", gap: 6 }}>
            <span style={{ width: 10, height: 10, borderRadius: "50%", background: l.color, display: "inline-block" }} />
            {l.label}
          </span>
        ))}
      </div>
    </div>
  );
}
