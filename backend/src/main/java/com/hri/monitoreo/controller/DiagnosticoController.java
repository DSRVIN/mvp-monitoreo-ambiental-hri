package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.DiagnosticoRequest;
import com.hri.monitoreo.entity.Diagnostico;
import com.hri.monitoreo.service.DiagnosticoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Diagnosticos", description = "Vincula paciente + enfermedad + zona + fecha; alimenta el Servicio de Analisis")
@RestController
@RequestMapping("/api/diagnosticos")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final DiagnosticoService service;

    @Operation(summary = "Listar diagnosticos paginados")
    @GetMapping
    public Page<Diagnostico> listar(@PageableDefault(size = 10) Pageable pageable) {
        return service.listar(pageable);
    }

    @PostMapping
    public ResponseEntity<Diagnostico> registrar(@Valid @RequestBody DiagnosticoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(request));
    }
}
