package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.SimulacionRequest;
import com.hri.monitoreo.entity.Alerta;
import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.entity.NivelAlerta;
import com.hri.monitoreo.entity.OrigenAlerta;
import com.hri.monitoreo.repository.AlertaRepository;
import com.hri.monitoreo.repository.DatoAmbientalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class SimulacionService {

    private final DatoAmbientalRepository datoRepository;
    private final AlertaRepository alertaRepository;

    public List<DatoAmbiental> generar(SimulacionRequest req) {
        int cantidad = req.cantidadOrDefault();
        int impacto = req.impactoOrDefault();
        double radioKm = req.radioOrDefault();
        double latCentro = req.latOrDefault();
        double lonCentro = req.lonOrDefault();

        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        List<DatoAmbiental> generados = new ArrayList<>();
        List<Alerta> alertas = new ArrayList<>();

        // 1 grado de latitud ~= 111 km
        double radioGrados = radioKm / 111.0;

        for (int i = 0; i < cantidad; i++) {
            double offsetLat = (rnd.nextDouble() * 2 - 1) * radioGrados;
            double offsetLon = (rnd.nextDouble() * 2 - 1) * radioGrados
                    / Math.cos(Math.toRadians(latCentro));
            double lat = latCentro + offsetLat;
            double lon = lonCentro + offsetLon;

            // El impacto (1-10) escala la concentracion de contaminantes
            double pm25 = redondear(rnd.nextDouble(5, 40) + impacto * rnd.nextDouble(8, 16));
            double pm10 = redondear(pm25 * rnd.nextDouble(1.2, 1.8));
            double o3 = redondear(rnd.nextDouble(10, 60) + impacto * 5);
            double no2 = redondear(rnd.nextDouble(10, 50) + impacto * 6);
            int ica = (int) Math.min(500, Math.round(pm25 * 2.5));

            DatoAmbiental dato = DatoAmbiental.builder()
                    .latitud(lat).longitud(lon)
                    .fechaMedicion(LocalDateTime.now().minusMinutes(rnd.nextInt(0, 1440)))
                    .pm25(pm25).pm10(pm10).o3(o3).no2(no2).co2(redondear(rnd.nextDouble(380, 600)))
                    .indiceCalidadAire(ica).fuente("Simulacion")
                    .build();
            generados.add(dato);

            if (req.generarAlertasOrDefault() && ica > 150) {
                alertas.add(Alerta.builder()
                        .titulo("Contaminacion critica simulada")
                        .descripcion("ICA " + ica + " detectado en punto simulado")
                        .nivel(ica > 300 ? NivelAlerta.CRITICO : NivelAlerta.ALTO)
                        .latitud(lat).longitud(lon)
                        .atendida(false)
                        .origen(OrigenAlerta.SIMULACION)
                        .build());
            }
        }

        List<DatoAmbiental> guardados = datoRepository.saveAll(generados);
        if (!alertas.isEmpty()) {
            alertaRepository.saveAll(alertas);
        }
        return guardados;
    }

    // Elimina los datos ambientales Y las alertas generadas por la simulacion
    public long limpiarSimulados() {
        List<DatoAmbiental> simulados = datoRepository.findAll().stream()
                .filter(d -> "Simulacion".equals(d.getFuente()))
                .toList();
        datoRepository.deleteAll(simulados);

        List<Alerta> alertasSimuladas = alertaRepository.findByOrigen(OrigenAlerta.SIMULACION);
        alertaRepository.deleteAll(alertasSimuladas);

        return simulados.size();
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
