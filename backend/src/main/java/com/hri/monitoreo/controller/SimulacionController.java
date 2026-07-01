package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.SimulacionRequest;
import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.service.SimulacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Simulacion", description = "Generacion de datos ambientales imaginarios para pruebas y demostracion")
@RestController
@RequestMapping("/api/simulacion")
@RequiredArgsConstructor
public class SimulacionController {

    private final SimulacionService service;

    @PostMapping("/datos-ambientales")
    public ResponseEntity<Map<String, Object>> generar(@RequestBody SimulacionRequest request) {
        List<DatoAmbiental> generados = service.generar(request);
        return ResponseEntity.ok(Map.of(
                "generados", generados.size(),
                "datos", generados
        ));
    }

    @DeleteMapping("/datos-ambientales")
    public ResponseEntity<Map<String, Object>> limpiar() {
        long eliminados = service.limpiarSimulados();
        return ResponseEntity.ok(Map.of("eliminados", eliminados));
    }
}
