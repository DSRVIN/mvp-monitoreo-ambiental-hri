package com.hri.monitoreo.controller;

import com.hri.monitoreo.dto.MapaCalorResponse;
import com.hri.monitoreo.service.MapaCalorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Datos para el mapa de calor de casos de dengue en Ica (vigilancia MINSA)
@Tag(name = "Mapa de calor", description = "Casos de dengue por distrito de Ica para el mapa de calor (datos reales del MINSA)")
@RestController
@RequestMapping("/api/mapa-calor")
@RequiredArgsConstructor
public class MapaCalorController {

    private final MapaCalorService service;

    @Operation(summary = "Obtener puntos de calor de dengue en Ica",
            description = "Si se indica el parametro 'anio', filtra los casos a ese anio; si no, devuelve el total 2015-2024")
    @GetMapping("/dengue")
    public MapaCalorResponse dengue(@RequestParam(required = false) String anio) {
        return service.obtenerDengue(anio);
    }
}
