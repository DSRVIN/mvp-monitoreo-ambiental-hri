package com.hri.monitoreo.controller;

import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.service.DatoAmbientalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Ambiental", description = "Servicio de datos ambientales: calidad del aire por zona y fecha")
@RestController
@RequestMapping("/api/ambiental")
@RequiredArgsConstructor
public class DatoAmbientalController {

    private final DatoAmbientalService service;

    @Operation(summary = "Listar datos ambientales, opcionalmente filtrados por rango de fechas (RF12)")
    @GetMapping
    public List<DatoAmbiental> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        if (desde != null && hasta != null) {
            return service.listarPorRango(desde, hasta);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public DatoAmbiental obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<DatoAmbiental> crear(@RequestBody DatoAmbiental dato) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dato));
    }

    @PutMapping("/{id}")
    public DatoAmbiental actualizar(@PathVariable Long id, @RequestBody DatoAmbiental dato) {
        return service.actualizar(id, dato);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
