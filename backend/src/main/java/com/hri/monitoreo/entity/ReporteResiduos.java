package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Reporte de zonas de riesgo por residuos solidos, proveniente de la Municipalidad de Ica.
// Se carga manualmente via panel administrativo (Capitulo 8.4), sin API automatizada.
@Entity
@Table(name = "reportes_residuos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResiduos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivelRiesgo;

    @Column(nullable = false)
    private LocalDate fechaReporte;

    private String reportadoPor;
}
