package com.hri.monitoreo.repository;

import com.hri.monitoreo.entity.Diagnostico;
import com.hri.monitoreo.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    List<Diagnostico> findByZonaAndFechaDiagnosticoGreaterThanEqual(Zona zona, LocalDate desde);
    List<Diagnostico> findByPacienteId(Long pacienteId);
}
