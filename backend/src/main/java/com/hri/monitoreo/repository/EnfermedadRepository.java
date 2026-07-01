package com.hri.monitoreo.repository;

import com.hri.monitoreo.entity.Enfermedad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnfermedadRepository extends JpaRepository<Enfermedad, Long> {
}
