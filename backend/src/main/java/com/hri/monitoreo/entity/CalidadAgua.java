package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Calidad del agua por cuenca, proveniente de la Autoridad Nacional del Agua (ANA).
// Se carga por lotes via archivo CSV (RF04/Capitulo 8.4), separado de DatoAmbiental
// porque mide parametros distintos (agua, no aire).
@Entity
@Table(name = "calidad_agua")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalidadAgua {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cuenca;

    private Double ph;
    private Double turbidezNtu;
    private Double coliformesFecales;

    @Column(nullable = false)
    private LocalDate fechaMuestreo;

    @Builder.Default
    @Column(nullable = false)
    private String fuente = "ANA (CSV)";
}
