package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.ZonaCriticaResponse;
import com.hri.monitoreo.entity.*;
import com.hri.monitoreo.repository.AlertaRepository;
import com.hri.monitoreo.repository.DatoAmbientalRepository;
import com.hri.monitoreo.repository.DiagnosticoRepository;
import com.hri.monitoreo.repository.ZonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

// Servicio de Analisis: orquesta el Servicio de Pacientes (via Diagnostico) y el Servicio
// Ambiental para calcular correlaciones por zona y disparar alertas tempranas (RF06, RF09, RN02).
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalisisService {

    // Umbral definido en la regla de negocio RN02: mas de 5 casos en una misma zona en 7 dias
    private static final long UMBRAL_CASOS = 5;
    private static final int VENTANA_DIAS = 7;

    private final ZonaRepository zonaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final DatoAmbientalRepository datoAmbientalRepository;
    private final AlertaRepository alertaRepository;

    public List<ZonaCriticaResponse> obtenerZonasCriticas() {
        return zonaRepository.findAll().stream()
                .map(this::calcularZonaCritica)
                .sorted(Comparator.comparingLong(ZonaCriticaResponse::casosUltimos7Dias).reversed())
                .toList();
    }

    // Ejecutado automaticamente cada 30 minutos (interoperabilidad asincrona, RF09)
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void ejecutarAnalisisProgramado() {
        log.info("Ejecutando analisis epidemiologico programado...");
        generarAlertasPorUmbral();
    }

    // Tambien puede dispararse manualmente (util para pruebas/demo sin esperar el ciclo de 30 min)
    public List<Alerta> generarAlertasPorUmbral() {
        return zonaRepository.findAll().stream()
                .map(zona -> {
                    ZonaCriticaResponse resultado = calcularZonaCritica(zona);
                    zona.setNivelRiesgo(resultado.nivelRiesgo());
                    zonaRepository.save(zona);
                    return crearAlertaSiCorresponde(zona, resultado);
                })
                .filter(a -> a != null)
                .toList();
    }

    private ZonaCriticaResponse calcularZonaCritica(Zona zona) {
        LocalDate desdeFecha = LocalDate.now().minusDays(VENTANA_DIAS);
        LocalDateTime desdeFechaHora = LocalDateTime.now().minusDays(VENTANA_DIAS);

        long casos = diagnosticoRepository
                .findByZonaAndFechaDiagnosticoGreaterThanEqual(zona, desdeFecha)
                .size();

        List<DatoAmbiental> datos = datoAmbientalRepository
                .findByZonaAndFechaMedicionGreaterThanEqual(zona, desdeFechaHora);

        OptionalDouble promedio = datos.stream()
                .map(DatoAmbiental::getIndiceCalidadAire)
                .filter(ica -> ica != null)
                .mapToInt(Integer::intValue)
                .average();
        Double icaPromedio = promedio.isPresent() ? Math.round(promedio.getAsDouble() * 10.0) / 10.0 : null;

        NivelAlerta nivel = calcularNivelRiesgo(casos, icaPromedio);

        return new ZonaCriticaResponse(
                zona.getId(), zona.getNombre(), zona.getLatitud(), zona.getLongitud(),
                nivel, casos, icaPromedio
        );
    }

    private NivelAlerta calcularNivelRiesgo(long casos, Double icaPromedio) {
        if (casos > UMBRAL_CASOS * 2) return NivelAlerta.CRITICO;
        if (casos > UMBRAL_CASOS) return NivelAlerta.ALTO;
        if (icaPromedio == null) return NivelAlerta.BAJO;
        if (icaPromedio <= 50) return NivelAlerta.BAJO;
        if (icaPromedio <= 100) return NivelAlerta.MEDIO;
        if (icaPromedio <= 150) return NivelAlerta.ALTO;
        return NivelAlerta.CRITICO;
    }

    private Alerta crearAlertaSiCorresponde(Zona zona, ZonaCriticaResponse resultado) {
        if (resultado.casosUltimos7Dias() <= UMBRAL_CASOS) return null;

        boolean yaExisteAlertaPendiente = !alertaRepository.findByZonaAndAtendidaFalse(zona).isEmpty();
        if (yaExisteAlertaPendiente) return null;

        Alerta alerta = Alerta.builder()
                .titulo("Concentracion anormal de casos en " + zona.getNombre())
                .descripcion("Se detectaron " + resultado.casosUltimos7Dias() +
                        " casos en los ultimos " + VENTANA_DIAS + " dias (umbral: " + UMBRAL_CASOS + "). " +
                        "ICA promedio de la zona: " + resultado.icaPromedio())
                .nivel(resultado.nivelRiesgo())
                .latitud(zona.getLatitud()).longitud(zona.getLongitud())
                .zona(zona)
                .atendida(false)
                .origen(OrigenAlerta.ANALISIS_AUTOMATICO)
                .build();

        Alerta guardada = alertaRepository.save(alerta);
        log.info("Alerta automatica generada para zona '{}': {} casos", zona.getNombre(), resultado.casosUltimos7Dias());
        return guardada;
    }
}
