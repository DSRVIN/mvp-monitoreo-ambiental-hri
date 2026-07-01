package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.AlertaRequest;
import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.entity.Enfermedad;
import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.AlertaRepository;
import com.hri.monitoreo.repository.EnfermedadRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository repository;
    private final ZonaRepository zonaRepository;
    private final EnfermedadRepository enfermedadRepository;

    public List<Alerta> listar() {
        return repository.findAll();
    }

    public List<Alerta> listarPendientes() {
        return repository.findByAtendidaFalse();
    }

    // Consulta historica de alertas por rango de fechas (RF12)
    public List<Alerta> listarPorRango(LocalDateTime desde, LocalDateTime hasta, boolean soloPendientes) {
        return soloPendientes
                ? repository.findByFechaGeneracionBetweenAndAtendidaFalse(desde, hasta)
                : repository.findByFechaGeneracionBetween(desde, hasta);
    }

    // Listado paginado para el dashboard, combinando filtros de estado y rango de fechas
    public Page<Alerta> buscarPaginado(Pageable pageable, boolean soloPendientes,
                                       LocalDateTime desde, LocalDateTime hasta) {
        if (desde != null && hasta != null) {
            return soloPendientes
                    ? repository.findByFechaGeneracionBetweenAndAtendidaFalse(desde, hasta, pageable)
                    : repository.findByFechaGeneracionBetween(desde, hasta, pageable);
        }
        return soloPendientes ? repository.findByAtendidaFalse(pageable) : repository.findAll(pageable);
    }

    public Alerta obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", id));
    }

    public Alerta crear(AlertaRequest request) {
        Zona zona = request.zonaId() != null
                ? zonaRepository.findById(request.zonaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Zona", request.zonaId()))
                : null;
        Enfermedad enfermedad = request.enfermedadId() != null
                ? enfermedadRepository.findById(request.enfermedadId())
                        .orElseThrow(() -> new ResourceNotFoundException("Enfermedad", request.enfermedadId()))
                : null;

        Alerta alerta = Alerta.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .nivel(request.nivel())
                .latitud(request.latitud() != null ? request.latitud() : (zona != null ? zona.getLatitud() : null))
                .longitud(request.longitud() != null ? request.longitud() : (zona != null ? zona.getLongitud() : null))
                .zona(zona)
                .enfermedad(enfermedad)
                .atendida(false)
                .build();

        return repository.save(alerta);
    }

    public Alerta marcarAtendida(Long id) {
        Alerta a = obtener(id);
        a.setAtendida(true);
        return repository.save(a);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }
}
