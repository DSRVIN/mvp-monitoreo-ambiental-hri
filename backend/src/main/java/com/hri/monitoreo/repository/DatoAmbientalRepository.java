package com.hri.monitoreo.repository;

import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.entity.Zona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DatoAmbientalRepository extends JpaRepository<DatoAmbiental, Long> {
    List<DatoAmbiental> findByFechaMedicionBetween(LocalDateTime desde, LocalDateTime hasta);
    List<DatoAmbiental> findByZonaAndFechaMedicionGreaterThanEqual(Zona zona, LocalDateTime desde);
}
