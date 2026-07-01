package com.hri.monitoreo.service;

import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.DatoAmbientalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatoAmbientalService {

    private final DatoAmbientalRepository repository;

    public List<DatoAmbiental> listar() {
        return repository.findAll();
    }

    public List<DatoAmbiental> listarPorRango(LocalDateTime desde, LocalDateTime hasta) {
        return repository.findByFechaMedicionBetween(desde, hasta);
    }

    public DatoAmbiental obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DatoAmbiental", id));
    }

    public DatoAmbiental crear(DatoAmbiental dato) {
        return repository.save(dato);
    }

    public DatoAmbiental actualizar(Long id, DatoAmbiental datos) {
        DatoAmbiental d = obtener(id);
        d.setLatitud(datos.getLatitud());
        d.setLongitud(datos.getLongitud());
        d.setFechaMedicion(datos.getFechaMedicion());
        d.setPm25(datos.getPm25());
        d.setPm10(datos.getPm10());
        d.setO3(datos.getO3());
        d.setNo2(datos.getNo2());
        d.setCo2(datos.getCo2());
        d.setIndiceCalidadAire(datos.getIndiceCalidadAire());
        d.setFuente(datos.getFuente());
        return repository.save(d);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }
}
