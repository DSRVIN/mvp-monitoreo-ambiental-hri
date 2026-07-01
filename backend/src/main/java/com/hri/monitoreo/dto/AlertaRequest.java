package com.hri.monitoreo.dto;

import com.hri.monitoreo.entity.NivelAlerta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AlertaRequest(
        @NotBlank String titulo,
        String descripcion,
        @NotNull NivelAlerta nivel,
        Long zonaId,
        Long enfermedadId,
        Double latitud,
        Double longitud
) {}
