package com.hri.monitoreo.controller;

import com.hri.monitoreo.entity.Enfermedad;
import com.hri.monitoreo.service.EnfermedadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Enfermedades", description = "Catalogo de enfermedades asociadas a contaminacion ambiental")
@RestController
@RequestMapping("/api/enfermedades")
@RequiredArgsConstructor
public class EnfermedadController {

    private final EnfermedadService service;

    @GetMapping
    public List<Enfermedad> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Enfermedad obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Enfermedad> crear(@RequestBody Enfermedad enfermedad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(enfermedad));
    }

    @PutMapping("/{id}")
    public Enfermedad actualizar(@PathVariable Long id, @RequestBody Enfermedad enfermedad) {
        return service.actualizar(id, enfermedad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
