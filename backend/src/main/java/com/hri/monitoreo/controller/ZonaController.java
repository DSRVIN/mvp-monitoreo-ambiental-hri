package com.hri.monitoreo.controller;

import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.repository.ZonaRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Zonas", description = "Catalogo de zonas geograficas (distritos de Ica)")
@RestController
@RequestMapping("/api/zonas")
@RequiredArgsConstructor
public class ZonaController {

    private final ZonaRepository repository;

    @GetMapping
    public List<Zona> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<Zona> crear(@RequestBody Zona zona) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(zona));
    }
}
