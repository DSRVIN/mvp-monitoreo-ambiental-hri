package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.AlertaRequest;
import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.service.AlertaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Alertas", description = "Servicio de alertas: manuales y generadas automaticamente por el Servicio de Analisis")
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService service;

    @Operation(summary = "Listar alertas paginadas",
            description = "Filtra por pendientes y/o por rango de fechas de generacion (RF12). " +
                    "Parametros de paginacion: page (base 0), size, sort")
    @GetMapping
    public Page<Alerta> listar(
            @RequestParam(required = false, defaultValue = "false") boolean soloPendientes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
            @PageableDefault(size = 10) Pageable pageable) {
        return service.buscarPaginado(pageable, soloPendientes, desde, hasta);
    }

    @GetMapping("/{id}")
    public Alerta obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Registrar alerta manual")
    @PostMapping
    public ResponseEntity<Alerta> crear(@Valid @RequestBody AlertaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @Operation(summary = "Marcar alerta como atendida")
    @PatchMapping("/{id}/atender")
    public Alerta atender(@PathVariable Long id) {
        return service.marcarAtendida(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
