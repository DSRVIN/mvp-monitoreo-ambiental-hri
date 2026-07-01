package com.hri.monitoreo.service;

import com.hri.monitoreo.entity.CalidadAgua;
import com.hri.monitoreo.entity.DatoAmbiental;
import com.hri.monitoreo.entity.NivelAlerta;
import com.hri.monitoreo.entity.Zona;
import com.hri.monitoreo.exception.ResourceNotFoundException;
import com.hri.monitoreo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// Pruebas de los conectores externos simulados (Capitulo 8.4): SENAMHI, ANA y Municipalidad
@ExtendWith(MockitoExtension.class)
class IntegracionExternaServiceTest {

    @Mock private ZonaRepository zonaRepository;
    @Mock private DatoAmbientalRepository datoAmbientalRepository;
    @Mock private CalidadAguaRepository calidadAguaRepository;
    @Mock private ReporteResiduosRepository reporteResiduosRepository;

    @InjectMocks
    private IntegracionExternaService service;

    private Zona zonaDePrueba() {
        return Zona.builder().id(1L).nombre("Subtanjalla").latitud(-14.03).longitud(-75.75)
                .nivelRiesgo(NivelAlerta.BAJO).build();
    }

    @Test
    void mockSenamhiDevuelveDatosDentroDeRangosRealistas() {
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zonaDePrueba()));

        var mock = service.consultarMockSenamhi(1L);

        assertThat(mock.zona()).isEqualTo("Subtanjalla");
        assertThat(mock.pm25()).isBetween(15.0, 90.0);
        assertThat(mock.pm10()).isBetween(25.0, 140.0);
    }

    @Test
    void consultarSenamhiConZonaInexistenteLanzaExcepcion() {
        when(zonaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarMockSenamhi(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void importarDesdeSenamhiPersisteUnDatoAmbiental() {
        Zona zona = zonaDePrueba();
        when(zonaRepository.findById(1L)).thenReturn(Optional.of(zona));
        when(datoAmbientalRepository.save(any(DatoAmbiental.class))).thenAnswer(inv -> inv.getArgument(0));

        DatoAmbiental resultado = service.importarDesdeSenamhi(1L);

        assertThat(resultado.getFuente()).isEqualTo("SENAMHI (mock)");
        assertThat(resultado.getZona()).isEqualTo(zona);
    }

    @Test
    void importaCsvDeAnaConFormatoValido() throws Exception {
        String contenido = "cuenca,ph,turbidez,coliformes,fecha\nRio Ica,7.2,12.5,80,2026-06-15\n";
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "calidad.csv", "text/csv", contenido.getBytes());
        when(calidadAguaRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<CalidadAgua> resultado = service.importarCsvAna(archivo);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCuenca()).isEqualTo("Rio Ica");
        assertThat(resultado.get(0).getPh()).isEqualTo(7.2);
    }

    @Test
    void rechazaCsvConColumnasFaltantes() {
        String contenido = "cuenca,ph,turbidez,coliformes,fecha\nRio Ica,7.2,12.5\n";
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "invalido.csv", "text/csv", contenido.getBytes());

        assertThatThrownBy(() -> service.importarCsvAna(archivo))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
