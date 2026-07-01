package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.PacienteRequest;
import com.hri.monitoreo.entity.Paciente;
import com.hri.monitoreo.service.PacienteService;
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

@Tag(name = "Pacientes", description = "Servicio de gestion de pacientes")
@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService service;

    @Operation(summary = "Listar pacientes paginados",
            description = "Parametros: page (base 0), size, sort. Por defecto 20 por pagina")
    @GetMapping
    public Page<Paciente> listar(@PageableDefault(size = 20) Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public Paciente obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Paciente> crear(@Valid @RequestBody PacienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public Paciente actualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
