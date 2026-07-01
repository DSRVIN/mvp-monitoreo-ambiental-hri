package com.hri.monitoreo.service;

import com.hri.monitoreo.entity.Usuario;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    public Usuario cambiarEstado(Long id, boolean activo) {
        Usuario u = obtener(id);
        u.setActivo(activo);
        return repository.save(u);
    }

    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }
}
