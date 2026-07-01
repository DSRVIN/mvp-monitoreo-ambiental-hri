package com.hri.monitoreo.controller;

import com.hri.monitoreo.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

// Reportes estadisticos exportables (RF11)
@Tag(name = "Reportes", description = "Generacion de reportes estadisticos exportables en CSV")
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @Operation(summary = "Descargar reporte CSV de zonas criticas (analisis epidemiologico vigente)")
    @GetMapping("/zonas-criticas/csv")
    public ResponseEntity<byte[]> reporteZonasCriticasCsv() {
        byte[] contenido = service.generarReporteZonasCriticasCsv().getBytes(StandardCharsets.UTF_8);
        return csvResponse(contenido, "reporte-zonas-criticas.csv");
    }

    @Operation(summary = "Descargar reporte CSV de alertas",
            description = "Si se especifican desde/hasta, filtra por rango de fechas (RF12)")
    @GetMapping("/alertas/csv")
    public ResponseEntity<byte[]> reporteAlertasCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        byte[] contenido = service.generarReporteAlertasCsv(desde, hasta).getBytes(StandardCharsets.UTF_8);
        return csvResponse(contenido, "reporte-alertas.csv");
    }

    private ResponseEntity<byte[]> csvResponse(byte[] contenido, String nombreArchivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(nombreArchivo).build());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(contenido);
    }
}
