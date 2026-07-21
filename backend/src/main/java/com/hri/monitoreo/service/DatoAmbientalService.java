package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.DatoAmbientalRequest;
import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.DatoAmbientalRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DatoAmbientalService {

    private final DatoAmbientalRepository repository;
    private final ZonaRepository zonaRepository;

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

    public DatoAmbiental crear(DatoAmbientalRequest request) {
        Zona zona = resolverZona(request.zonaId());
        DatoAmbiental dato = DatoAmbiental.builder()
                .zona(zona)
                .latitud(coordenada(request.latitud(), zona, true))
                .longitud(coordenada(request.longitud(), zona, false))
                .fechaMedicion(request.fechaMedicion() != null ? request.fechaMedicion() : LocalDateTime.now())
                .pm25(request.pm25()).pm10(request.pm10()).o3(request.o3())
                .no2(request.no2()).co2(request.co2())
                .indiceCalidadAire(request.indiceCalidadAire())
                .fuente(request.fuente() != null ? request.fuente() : "Registro manual")
                .build();
        return repository.save(dato);
    }

    public DatoAmbiental actualizar(Long id, DatoAmbientalRequest request) {
        DatoAmbiental d = obtener(id);
        Zona zona = resolverZona(request.zonaId());
        if (zona != null) d.setZona(zona);
        d.setLatitud(coordenada(request.latitud(), zona, true));
        d.setLongitud(coordenada(request.longitud(), zona, false));
        if (request.fechaMedicion() != null) d.setFechaMedicion(request.fechaMedicion());
        d.setPm25(request.pm25());
        d.setPm10(request.pm10());
        d.setO3(request.o3());
        d.setNo2(request.no2());
        d.setCo2(request.co2());
        d.setIndiceCalidadAire(request.indiceCalidadAire());
        if (request.fuente() != null) d.setFuente(request.fuente());
        return repository.save(d);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private Zona resolverZona(Long zonaId) {
        if (zonaId == null) return null;
        return zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResourceNotFoundException("Zona", zonaId));
    }

    // Si no se envia coordenada explicita, se usa la de la zona seleccionada
    private Double coordenada(Double valor, Zona zona, boolean esLatitud) {
        if (valor != null) return valor;
        if (zona == null) return null;
        return esLatitud ? zona.getLatitud() : zona.getLongitud();
    }
}
