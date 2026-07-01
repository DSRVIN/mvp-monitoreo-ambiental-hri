package com.hri.monitoreo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// Vincula un paciente con una enfermedad detectada en una fecha y zona determinada.
// Es la base para el analisis de correlacion epidemiologica (RF06, RN02).
@Entity
@Table(name = "diagnosticos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enfermedad_id", nullable = false)
    private Enfermedad enfermedad;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false)
    private LocalDate fechaDiagnostico;

    @PrePersist
    void onCreate() {
        if (this.fechaDiagnostico == null) {
            this.fechaDiagnostico = LocalDate.now();
        }
    }
}
