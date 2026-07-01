package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivel;

    private Double latitud;
    private Double longitud;

    // Enfermedad asociada a la alerta (opcional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enfermedad_id")
    private Enfermedad enfermedad;

    // Zona cuyo analisis disparo la alerta (opcional; se usa para alertas automaticas)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Builder.Default
    @Column(nullable = false)
    private boolean atendida = false;

    // Traza el origen de la alerta para poder limpiar/filtrar por fuente (RN05: historial de alertas)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private OrigenAlerta origen = OrigenAlerta.MANUAL;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @PrePersist
    void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
}
