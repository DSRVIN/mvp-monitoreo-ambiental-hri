package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.ZonaCriticaResponse;
import com.hri.monitoreo.entity.*;
import com.hri.monitoreo.repository.AlertaRepository;
import com.hri.monitoreo.repository.DatoAmbientalRepository;
import com.hri.monitoreo.repository.DiagnosticoRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Pruebas unitarias del Servicio de Analisis: valida la correlacion por zona (RF06)
// y la regla de negocio RN02 (mas de 5 casos en 7 dias genera una alerta).
@ExtendWith(MockitoExtension.class)
class AnalisisServiceTest {

    @Mock private ZonaRepository zonaRepository;
    @Mock private DiagnosticoRepository diagnosticoRepository;
    @Mock private DatoAmbientalRepository datoAmbientalRepository;
    @Mock private AlertaRepository alertaRepository;

    @InjectMocks
    private AnalisisService analisisService;

    private Zona zonaDePrueba() {
        return Zona.builder().id(1L).nombre("Subtanjalla").latitud(-14.0339).longitud(-75.7519)
                .nivelRiesgo(NivelAlerta.BAJO).build();
    }

    private Diagnostico diagnosticoDePrueba(Zona zona) {
        return Diagnostico.builder().id(1L).zona(zona).fechaDiagnostico(LocalDate.now()).build();
    }

    private DatoAmbiental datoConIca(Zona zona, int ica) {
        return DatoAmbiental.builder().zona(zona).fechaMedicion(LocalDateTime.now())
                .indiceCalidadAire(ica).build();
    }

    @Test
    void zonaConMasDeCincoCasosEsNivelAlto() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findAll()).thenReturn(List.of(zona));
        when(diagnosticoRepository.findByZonaAndFechaDiagnosticoGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of(
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona),
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona)));
        when(datoAmbientalRepository.findByZonaAndFechaMedicionGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());

        List<ZonaCriticaResponse> resultado = analisisService.obtenerZonasCriticas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).casosUltimos7Dias()).isEqualTo(6);
        assertThat(resultado.get(0).nivelRiesgo()).isEqualTo(NivelAlerta.ALTO);
    }

    @Test
    void zonaSinCasosPeroConIcaAltoEsCritica() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findAll()).thenReturn(List.of(zona));
        when(diagnosticoRepository.findByZonaAndFechaDiagnosticoGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());
        when(datoAmbientalRepository.findByZonaAndFechaMedicionGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of(datoConIca(zona, 200)));

        List<ZonaCriticaResponse> resultado = analisisService.obtenerZonasCriticas();

        assertThat(resultado.get(0).nivelRiesgo()).isEqualTo(NivelAlerta.CRITICO);
        assertThat(resultado.get(0).icaPromedio()).isEqualTo(200.0);
    }

    @Test
    void generaAlertaAutomaticaAlSuperarElUmbral() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findAll()).thenReturn(List.of(zona));
        when(diagnosticoRepository.findByZonaAndFechaDiagnosticoGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of(
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona),
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona)));
        when(datoAmbientalRepository.findByZonaAndFechaMedicionGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());
        when(alertaRepository.findByZonaAndAtendidaFalse(zona)).thenReturn(List.of());
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Alerta> nuevas = analisisService.generarAlertasPorUmbral();

        assertThat(nuevas).hasSize(1);
        assertThat(nuevas.get(0).getOrigen()).isEqualTo(OrigenAlerta.ANALISIS_AUTOMATICO);
        assertThat(nuevas.get(0).getZona()).isEqualTo(zona);
        verify(alertaRepository, times(1)).save(any(Alerta.class));
    }

    @Test
    void noDuplicaAlertaSiYaExisteUnaPendienteParaLaZona() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findAll()).thenReturn(List.of(zona));
        when(diagnosticoRepository.findByZonaAndFechaDiagnosticoGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of(
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona),
                        diagnosticoDePrueba(zona), diagnosticoDePrueba(zona), diagnosticoDePrueba(zona)));
        when(datoAmbientalRepository.findByZonaAndFechaMedicionGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());
        when(alertaRepository.findByZonaAndAtendidaFalse(zona))
                .thenReturn(List.of(Alerta.builder().id(99L).build()));

        List<Alerta> nuevas = analisisService.generarAlertasPorUmbral();

        assertThat(nuevas).isEmpty();
        verify(alertaRepository, never()).save(any(Alerta.class));
    }

    @Test
    void zonaSinDatosNiCasosEsBajo() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findAll()).thenReturn(List.of(zona));
        when(diagnosticoRepository.findByZonaAndFechaDiagnosticoGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());
        when(datoAmbientalRepository.findByZonaAndFechaMedicionGreaterThanEqual(eq(zona), any()))
                .thenReturn(List.of());

        List<ZonaCriticaResponse> resultado = analisisService.obtenerZonasCriticas();

        assertThat(resultado.get(0).nivelRiesgo()).isEqualTo(NivelAlerta.BAJO);
        assertThat(resultado.get(0).icaPromedio()).isNull();
    }
}
