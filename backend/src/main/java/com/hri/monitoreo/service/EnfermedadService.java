package com.hri.monitoreo.service;

import com.hri.monitoreo.entity.Enfermedad;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.EnfermedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnfermedadService {

    private final EnfermedadRepository repository;

    public List<Enfermedad> listar() {
        return repository.findAll();
    }

    public Enfermedad obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enfermedad", id));
    }

    public Enfermedad crear(Enfermedad enfermedad) {
        return repository.save(enfermedad);
    }

    public Enfermedad actualizar(Long id, Enfermedad datos) {
        Enfermedad e = obtener(id);
        e.setNombre(datos.getNombre());
        e.setCodigoCie10(datos.getCodigoCie10());
        e.setDescripcion(datos.getDescripcion());
        e.setContaminanteAsociado(datos.getContaminanteAsociado());
        return repository.save(e);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }
}
