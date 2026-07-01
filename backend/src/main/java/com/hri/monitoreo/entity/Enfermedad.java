package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enfermedades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enfermedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true)
    private String codigoCie10;

    @Column(length = 1000)
    private String descripcion;

    // Contaminante principal asociado (ej: PM2.5, O3, NO2)
    private String contaminanteAsociado;
}
