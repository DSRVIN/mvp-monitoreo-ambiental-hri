package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.PacienteRequest;
import com.hri.monitoreo.entity.Paciente;
import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.PacienteRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repository;
    private final ZonaRepository zonaRepository;

    public Page<Paciente> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Paciente obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", id));
    }

    public Paciente crear(PacienteRequest request) {
        Zona zona = resolverZona(request.zonaId());
        Paciente paciente = Paciente.builder()
                .nombres(request.nombres())
                .apellidos(request.apellidos())
                .documento(request.documento())
                .fechaNacimiento(request.fechaNacimiento())
                .sexo(request.sexo())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .latitud(request.latitud() != null ? request.latitud() : (zona != null ? zona.getLatitud() : null))
                .longitud(request.longitud() != null ? request.longitud() : (zona != null ? zona.getLongitud() : null))
                .zona(zona)
                .build();
        return repository.save(paciente);
    }

    public Paciente actualizar(Long id, PacienteRequest request) {
        Paciente p = obtener(id);
        Zona zona = resolverZona(request.zonaId());
        p.setNombres(request.nombres());
        p.setApellidos(request.apellidos());
        p.setDocumento(request.documento());
        p.setFechaNacimiento(request.fechaNacimiento());
        p.setSexo(request.sexo());
        p.setDireccion(request.direccion());
        p.setTelefono(request.telefono());
        if (request.latitud() != null) p.setLatitud(request.latitud());
        if (request.longitud() != null) p.setLongitud(request.longitud());
        if (zona != null) p.setZona(zona);
        return repository.save(p);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private Zona resolverZona(Long zonaId) {
        if (zonaId == null) return null;
        return zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResourceNotFoundException("Zona", zonaId));
    }
}
