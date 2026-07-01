package com.hri.monitoreo.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String documento,
        LocalDate fechaNacimiento,
        String sexo,
        String direccion,
        String telefono,
        Double latitud,
        Double longitud,
        Long zonaId
) {}
