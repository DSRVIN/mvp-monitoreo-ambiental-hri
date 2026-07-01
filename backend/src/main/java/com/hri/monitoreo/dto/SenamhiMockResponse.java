package com.hri.monitoreo.dto;

import java.time.LocalDateTime;

// Replica el formato de respuesta esperado del API real de SENAMHI (Capitulo 8.4).
// Permite validar el conector de integracion sin depender de credenciales institucionales.
public record SenamhiMockResponse(
        String estacion,
        String zona,
        LocalDateTime fecha,
        Double pm25,
        Double pm10,
        Double temperaturaC,
        Double humedadRelativa
) {}
