package com.hri.monitoreo.dto;

import java.util.Map;

// Un punto del mapa de calor: un distrito con su ubicacion y numero de casos
public record PuntoCalor(
        String ubigeo,
        String distrito,
        String provincia,
        double lat,
        double lng,
        long casos,
        Map<String, Long> porAnio,
        Map<String, Long> severidad
) {}
