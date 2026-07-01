package com.hri.monitoreo.dto;

import com.hri.monitoreo.entity.NivelAlerta;

// Resultado agregado del Servicio de Analisis para el endpoint /api/analisis/zonas-criticas
public record ZonaCriticaResponse(
        Long zonaId,
        String nombre,
        Double latitud,
        Double longitud,
        NivelAlerta nivelRiesgo,
        long casosUltimos7Dias,
        Double icaPromedio
) {}
