package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.ReporteResiduosRequest;
import com.hri.monitoreo.dto.SenamhiMockResponse;
import com.hri.monitoreo.entity.*;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// Conectores de integracion con entidades externas (Capitulo 8.4). Debido a las
// limitaciones de acceso a informacion ambiental real, se implementan como conectores
// que replican el formato esperado de cada fuente, dejando el sistema preparado para
// conectarse a las APIs/reportes reales en una futura fase de produccion.
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegracionExternaService {

    private final ZonaRepository zonaRepository;
    private final DatoAmbientalRepository datoAmbientalRepository;
    private final CalidadAguaRepository calidadAguaRepository;
    private final ReporteResiduosRepository reporteResiduosRepository;

    // --- SENAMHI: calidad de aire, temperatura y humedad (mock REST) ---

    public SenamhiMockResponse consultarMockSenamhi(Long zonaId) {
        Zona zona = obtenerZona(zonaId);
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        return new SenamhiMockResponse(
                "SENAMHI-ICA-" + zona.getId(),
                zona.getNombre(),
                LocalDateTime.now(),
                redondear(rnd.nextDouble(15, 90)),
                redondear(rnd.nextDouble(25, 140)),
                redondear(rnd.nextDouble(18, 32)),
                redondear(rnd.nextDouble(30, 70))
        );
    }

    public DatoAmbiental importarDesdeSenamhi(Long zonaId) {
        Zona zona = obtenerZona(zonaId);
        SenamhiMockResponse mock = consultarMockSenamhi(zonaId);

        DatoAmbiental dato = DatoAmbiental.builder()
                .zona(zona)
                .latitud(zona.getLatitud()).longitud(zona.getLongitud())
                .fechaMedicion(mock.fecha())
                .pm25(mock.pm25()).pm10(mock.pm10())
                .indiceCalidadAire((int) Math.round(mock.pm25() * 2.5))
                .fuente("SENAMHI (mock)")
                .build();

        DatoAmbiental guardado = datoAmbientalRepository.save(dato);
        log.info("Dato ambiental importado desde conector SENAMHI para zona '{}'", zona.getNombre());
        return guardado;
    }

    // --- ANA: calidad del agua por cuenca (carga CSV) ---
    // Formato esperado: cuenca,ph,turbidez,coliformes,fecha (yyyy-MM-dd), con encabezado

    public List<CalidadAgua> importarCsvAna(MultipartFile archivo) {
        List<CalidadAgua> registros = new java.util.ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(archivo.getInputStream(), StandardCharsets.UTF_8))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                if (linea.isBlank()) continue;

                String[] campos = linea.split(",");
                if (campos.length < 5) {
                    throw new IllegalArgumentException(
                            "Fila CSV invalida, se esperan 5 columnas (cuenca,ph,turbidez,coliformes,fecha): " + linea);
                }

                registros.add(CalidadAgua.builder()
                        .cuenca(campos[0].trim())
                        .ph(Double.parseDouble(campos[1].trim()))
                        .turbidezNtu(Double.parseDouble(campos[2].trim()))
                        .coliformesFecales(Double.parseDouble(campos[3].trim()))
                        .fechaMuestreo(LocalDate.parse(campos[4].trim()))
                        .build());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo CSV: " + e.getMessage());
        }

        List<CalidadAgua> guardados = calidadAguaRepository.saveAll(registros);
        log.info("Importados {} registros de calidad de agua desde CSV (ANA)", guardados.size());
        return guardados;
    }

    public List<CalidadAgua> listarCalidadAgua() {
        return calidadAguaRepository.findAll();
    }

    // --- Municipalidad de Ica: zonas de riesgo por residuos solidos (carga manual) ---

    public ReporteResiduos registrarReporteResiduos(ReporteResiduosRequest request) {
        Zona zona = request.zonaId() != null ? obtenerZona(request.zonaId()) : null;

        ReporteResiduos reporte = ReporteResiduos.builder()
                .zona(zona)
                .descripcion(request.descripcion())
                .nivelRiesgo(request.nivelRiesgo())
                .fechaReporte(request.fechaReporte() != null ? request.fechaReporte() : LocalDate.now())
                .reportadoPor(request.reportadoPor())
                .build();

        return reporteResiduosRepository.save(reporte);
    }

    public List<ReporteResiduos> listarReportesResiduos() {
        return reporteResiduosRepository.findAll();
    }

    private Zona obtenerZona(Long zonaId) {
        return zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResourceNotFoundException("Zona", zonaId));
    }

    private double redondear(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }
}
