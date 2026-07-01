package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "datos_ambientales")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatoAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(nullable = false)
    private LocalDateTime fechaMedicion;

    // Zona geografica a la que pertenece la medicion, usada para el analisis por distrito
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zona_id")
    private Zona zona;

    // Concentraciones de contaminantes
    private Double pm25;
    private Double pm10;
    private Double o3;
    private Double no2;
    private Double co2;

    // Indice de Calidad del Aire calculado
    private Integer indiceCalidadAire;

    // Origen del dato (estacion, sensor, API externa, etc.)
    private String fuente;

    @PrePersist
    void onCreate() {
        if (this.fechaMedicion == null) {
            this.fechaMedicion = LocalDateTime.now();
        }
    }
}
