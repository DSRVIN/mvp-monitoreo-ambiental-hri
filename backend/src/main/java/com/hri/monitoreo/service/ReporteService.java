package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.ZonaCriticaResponse;
import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Generacion de reportes estadisticos exportables en CSV (RF11)
@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AnalisisService analisisService;
    private final AlertaRepository alertaRepository;

    public String generarReporteZonasCriticasCsv() {
        List<ZonaCriticaResponse> zonas = analisisService.obtenerZonasCriticas();

        StringBuilder csv = new StringBuilder("zona,nivel_riesgo,casos_ultimos_7_dias,ica_promedio\n");
        for (ZonaCriticaResponse z : zonas) {
            csv.append(escapar(z.nombre())).append(',')
                    .append(z.nivelRiesgo()).append(',')
                    .append(z.casosUltimos7Dias()).append(',')
                    .append(z.icaPromedio() != null ? z.icaPromedio() : "").append('\n');
        }
        return csv.toString();
    }

    public String generarReporteAlertasCsv(LocalDateTime desde, LocalDateTime hasta) {
        List<Alerta> alertas = (desde != null && hasta != null)
                ? alertaRepository.findByFechaGeneracionBetween(desde, hasta)
                : alertaRepository.findAll();

        StringBuilder csv = new StringBuilder("id,titulo,nivel,zona,origen,atendida,fecha_generacion\n");
        for (Alerta a : alertas) {
            csv.append(a.getId()).append(',')
                    .append(escapar(a.getTitulo())).append(',')
                    .append(a.getNivel()).append(',')
                    .append(escapar(a.getZona() != null ? a.getZona().getNombre() : "")).append(',')
                    .append(a.getOrigen()).append(',')
                    .append(a.isAtendida()).append(',')
                    .append(a.getFechaGeneracion().format(FORMATO_FECHA)).append('\n');
        }
        return csv.toString();
    }

    // Envuelve en comillas los valores que contienen comas, comillas o saltos de linea (CSV estandar)
    private String escapar(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
