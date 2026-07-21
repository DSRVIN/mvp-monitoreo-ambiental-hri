package com.hri.monitoreo.dto;

import java.time.LocalDateTime;

public record DatoAmbientalRequest(
        Long zonaId,
        LocalDateTime fechaMedicion,
        Double latitud,
        Double longitud,
        Double pm25,
        Double pm10,
        Double o3,
        Double no2,
        Double co2,
        Integer indiceCalidadAire,
        String fuente
) {}
