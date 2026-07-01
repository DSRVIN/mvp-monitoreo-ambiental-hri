package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.DiagnosticoRequest;
import com.hri.monitoreo.entity.Diagnostico;
import com.hri.monitoreo.entity.Enfermedad;
import com.hri.monitoreo.entity.Paciente;
import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.DiagnosticoRepository;
import com.hri.monitoreo.repository.EnfermedadRepository;
import com.hri.monitoreo.repository.PacienteRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// Registra el vinculo Paciente-Enfermedad-Zona-Fecha que alimenta el Servicio de Analisis
@Service
@RequiredArgsConstructor
public class DiagnosticoService {

    private final DiagnosticoRepository diagnosticoRepository;
    private final PacienteRepository pacienteRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final ZonaRepository zonaRepository;

    public Page<Diagnostico> listar(Pageable pageable) {
        return diagnosticoRepository.findAll(pageable);
    }

    public Diagnostico registrar(DiagnosticoRequest request) {
        Paciente paciente = pacienteRepository.findById(request.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", request.pacienteId()));
        Enfermedad enfermedad = enfermedadRepository.findById(request.enfermedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Enfermedad", request.enfermedadId()));

        // Si no se especifica zona, se usa la zona de residencia del paciente
        Zona zona = null;
        if (request.zonaId() != null) {
            zona = zonaRepository.findById(request.zonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Zona", request.zonaId()));
        } else if (paciente.getZona() != null) {
            zona = paciente.getZona();
        }

        Diagnostico diagnostico = Diagnostico.builder()
                .paciente(paciente).enfermedad(enfermedad).zona(zona)
                .fechaDiagnostico(request.fechaDiagnostico())
                .build();

        return diagnosticoRepository.save(diagnostico);
    }
}
