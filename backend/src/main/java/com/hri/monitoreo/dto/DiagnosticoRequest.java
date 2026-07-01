package com.hri.monitoreo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DiagnosticoRequest(
        @NotNull Long pacienteId,
        @NotNull Long enfermedadId,
        Long zonaId,
        LocalDate fechaDiagnostico
) {}
