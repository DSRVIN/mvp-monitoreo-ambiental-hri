package com.hri.monitoreo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

// Respuesta del mapa de calor: metadatos + puntos por distrito
@JsonIgnoreProperties(ignoreUnknown = true)
public record MapaCalorResponse(
        Map<String, Object> meta,
        List<PuntoCalor> puntos
) {}
