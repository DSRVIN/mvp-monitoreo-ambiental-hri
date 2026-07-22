package com.hri.monitoreo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hri.monitoreo.dto.MapaCalorResponse;
import com.hri.monitoreo.dto.PuntoCalor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Sirve los datos agregados de casos de dengue en Ica para el mapa de calor.
// Los datos provienen de la vigilancia epidemiologica real del MINSA, procesados
// por el script scripts/generar_datos_dengue.py y almacenados como recurso.
@Service
@Slf4j
public class MapaCalorService {

    private static final String RECURSO = "data/dengue-ica.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private MapaCalorResponse cache;

    public MapaCalorResponse obtenerDengue(String anio) {
        MapaCalorResponse datos = cargar();
        if (anio == null || anio.isBlank()) {
            return datos;
        }
        // Filtra: los casos de cada distrito pasan a ser los del anio solicitado
        List<PuntoCalor> filtrados = new ArrayList<>();
        for (PuntoCalor p : datos.puntos()) {
            long casosAnio = p.porAnio().getOrDefault(anio, 0L);
            if (casosAnio > 0) {
                filtrados.add(new PuntoCalor(p.ubigeo(), p.distrito(), p.provincia(),
                        p.lat(), p.lng(), casosAnio, p.porAnio(), p.severidad()));
            }
        }
        Map<String, Object> meta = new HashMap<>(datos.meta());
        meta.put("anioFiltrado", anio);
        meta.put("totalCasos", filtrados.stream().mapToLong(PuntoCalor::casos).sum());
        return new MapaCalorResponse(meta, filtrados);
    }

    private MapaCalorResponse cargar() {
        if (cache != null) {
            return cache;
        }
        try (InputStream is = new ClassPathResource(RECURSO).getInputStream()) {
            cache = mapper.readValue(is, MapaCalorResponse.class);
            return cache;
        } catch (IOException e) {
            log.error("No se pudo cargar el recurso {}: {}", RECURSO, e.getMessage());
            return new MapaCalorResponse(Map.of("error", "datos no disponibles"), List.of());
        }
    }
}
