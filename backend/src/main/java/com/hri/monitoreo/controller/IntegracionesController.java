package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.ReporteResiduosRequest;
import com.hri.monitoreo.dto.SenamhiMockResponse;
import com.hri.monitoreo.entity.CalidadAgua;
import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.entity.ReporteResiduos;
import com.hri.monitoreo.service.IntegracionExternaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Conectores de integracion con entidades externas (SENAMHI, ANA, Municipalidad de Ica)
// descritos en el Capitulo 8.4 del informe del proyecto.
@Tag(name = "Integraciones", description = "Conectores simulados con SENAMHI, ANA y Municipalidad de Ica (Capitulo 8.4)")
@RestController
@RequestMapping("/api/integraciones")
@RequiredArgsConstructor
public class IntegracionesController {

    private final IntegracionExternaService service;

    // --- SENAMHI (mock REST) ---

    @GetMapping("/senamhi/mock")
    public SenamhiMockResponse consultarSenamhi(@RequestParam Long zonaId) {
        return service.consultarMockSenamhi(zonaId);
    }

    @PostMapping("/senamhi/importar")
    public ResponseEntity<DatoAmbiental> importarSenamhi(@RequestParam Long zonaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importarDesdeSenamhi(zonaId));
    }

    // --- ANA (carga CSV) ---

    @PostMapping(value = "/ana/csv", consumes = "multipart/form-data")
    public ResponseEntity<List<CalidadAgua>> importarCsvAna(@RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.importarCsvAna(archivo));
    }

    @GetMapping("/ana/calidad-agua")
    public List<CalidadAgua> listarCalidadAgua() {
        return service.listarCalidadAgua();
    }

    // --- Municipalidad de Ica (carga manual) ---

    @PostMapping("/municipalidad/reportes-residuos")
    public ResponseEntity<ReporteResiduos> registrarReporteResiduos(@Valid @RequestBody ReporteResiduosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarReporteResiduos(request));
    }

    @GetMapping("/municipalidad/reportes-residuos")
    public List<ReporteResiduos> listarReportesResiduos() {
        return service.listarReportesResiduos();
    }
}
