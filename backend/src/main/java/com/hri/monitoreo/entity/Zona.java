package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "zonas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Distrito o area geografica (ej: Ica, Subtanjalla, Parcona)
    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    // Nivel de riesgo ambiental asignado a la zona (se recalcula con el analisis)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NivelAlerta nivelRiesgo = NivelAlerta.BAJO;
}
