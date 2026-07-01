package com.hri.monitoreo.dto;

// Parametros para generar datos ambientales imaginarios
public record SimulacionRequest(
        Integer cantidad,      // cuantos puntos generar
        Integer impacto,       // intensidad de contaminacion (1-10)
        Double radioKm,        // dispersion geografica alrededor del centro
        Double latitud,        // centro (por defecto Ica)
        Double longitud,
        Boolean generarAlertas // crear alertas para los puntos criticos
) {
    public int cantidadOrDefault() { return cantidad == null ? 20 : Math.min(Math.max(cantidad, 1), 1000); }
    public int impactoOrDefault() { return impacto == null ? 5 : Math.min(Math.max(impacto, 1), 10); }
    public double radioOrDefault() { return radioKm == null ? 8.0 : Math.min(Math.max(radioKm, 0.1), 100.0); }
    public double latOrDefault() { return latitud == null ? -14.0678 : latitud; }
    public double lonOrDefault() { return longitud == null ? -75.7286 : longitud; }
    public boolean generarAlertasOrDefault() { return generarAlertas != null && generarAlertas; }
}
