package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.ZonaCriticaResponse;
import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.service.AnalisisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Analisis", description = "Servicio de analisis epidemiologico: correlacion por zona y generacion de alertas tempranas")
@RestController
@RequestMapping("/api/analisis")
@RequiredArgsConstructor
public class AnalisisController {

    private final AnalisisService service;

    @Operation(summary = "Obtener el analisis de correlacion por zona (casos ultimos 7 dias + ICA promedio)")
    @GetMapping("/zonas-criticas")
    public List<ZonaCriticaResponse> zonasCriticas() {
        return service.obtenerZonasCriticas();
    }

    @Operation(summary = "Ejecutar el analisis manualmente y generar alertas si corresponde",
            description = "Equivalente al job programado que corre cada 30 minutos, util para pruebas/demo")
    @PostMapping("/ejecutar")
    public List<Alerta> ejecutar() {
        return service.generarAlertasPorUmbral();
    }
}
