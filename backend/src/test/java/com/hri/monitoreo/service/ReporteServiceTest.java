package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.ZonaCriticaResponse;
import com.hri.monitoreo.entity.*;
import com.hri.monitoreo.repository.AlertaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// Pruebas de generacion de reportes CSV (RF11)
@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock private AnalisisService analisisService;
    @Mock private AlertaRepository alertaRepository;

    @InjectMocks
    private ReporteService reporteService;

    @Test
    void generaCsvDeZonasCriticasConEncabezadoYFilas() {
        when(analisisService.obtenerZonasCriticas()).thenReturn(List.of(
                new ZonaCriticaResponse(1L, "Subtanjalla", -14.03, -75.75, NivelAlerta.ALTO, 6, 155.0)
        ));

        String csv = reporteService.generarReporteZonasCriticasCsv();

        assertThat(csv).startsWith("zona,nivel_riesgo,casos_ultimos_7_dias,ica_promedio\n");
        assertThat(csv).contains("Subtanjalla,ALTO,6,155.0");
    }

    @Test
    void generaCsvDeAlertasEscapandoComasEnElTitulo() {
        Alerta alerta = Alerta.builder()
                .id(1L).titulo("Riesgo alto, revisar zona").nivel(NivelAlerta.ALTO)
                .origen(OrigenAlerta.MANUAL).atendida(false)
                .fechaGeneracion(LocalDateTime.of(2026, 6, 30, 10, 0))
                .build();
        when(alertaRepository.findAll()).thenReturn(List.of(alerta));

        String csv = reporteService.generarReporteAlertasCsv(null, null);

        assertThat(csv).contains("\"Riesgo alto, revisar zona\"");
        assertThat(csv).contains("2026-06-30 10:00");
    }

    @Test
    void filtraAlertasPorRangoDeFechasCuandoSeEspecifica() {
        LocalDateTime desde = LocalDateTime.of(2026, 6, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2026, 6, 30, 23, 59);
        when(alertaRepository.findByFechaGeneracionBetween(desde, hasta)).thenReturn(List.of());

        String csv = reporteService.generarReporteAlertasCsv(desde, hasta);

        assertThat(csv.lines().count()).isEqualTo(1); // solo el encabezado
    }
}
