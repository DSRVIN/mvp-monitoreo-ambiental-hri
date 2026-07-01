package com.hri.monitoreo.dto;

import com.hri.monitoreo.entity.NivelAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReporteResiduosRequest(
        Long zonaId,
        @NotBlank String descripcion,
        @NotNull NivelAlerta nivelRiesgo,
        LocalDate fechaReporte,
        String reportadoPor
) {}
