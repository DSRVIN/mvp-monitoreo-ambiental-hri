package com.hri.monitoreo.repository;

import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.entity.NivelAlerta;
import com.hri.monitoreo.entity.OrigenAlerta;
import com.hri.monitoreo.entity.Zona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByAtendidaFalse();
    List<Alerta> findByNivel(NivelAlerta nivel);
    List<Alerta> findByZonaAndAtendidaFalse(Zona zona);
    List<Alerta> findByOrigen(OrigenAlerta origen);

    // Consulta historica de alertas por rango de fechas (RF12)
    List<Alerta> findByFechaGeneracionBetween(LocalDateTime desde, LocalDateTime hasta);
    List<Alerta> findByFechaGeneracionBetweenAndAtendidaFalse(LocalDateTime desde, LocalDateTime hasta);

    // Variantes paginadas para el listado del dashboard (paginacion de tablas grandes)
    Page<Alerta> findByAtendidaFalse(Pageable pageable);
    Page<Alerta> findByFechaGeneracionBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
    Page<Alerta> findByFechaGeneracionBetweenAndAtendidaFalse(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);
}
