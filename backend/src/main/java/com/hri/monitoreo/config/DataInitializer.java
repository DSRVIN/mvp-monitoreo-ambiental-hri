package com.hri.monitoreo.config;

import com.hri.monitoreo.entity.*;
import com.hri.monitoreo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Carga datos de ejemplo representativos del entorno hospitalario y ambiental de la
// region Ica (escenario descrito en el informe del proyecto), ya que no se cuenta con
// acceso a datos clinicos y ambientales reales (ver Capitulo 1.7, Limitaciones).
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PacienteRepository pacienteRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final ZonaRepository zonaRepository;
    private final DatoAmbientalRepository datoAmbientalRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final AlertaRepository alertaRepository;
    private final CalidadAguaRepository calidadAguaRepository;
    private final ReporteResiduosRepository reporteResiduosRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seed() {
        return args -> {
            seedUsuarios();
            Map<String, Zona> zonas = seedZonas();
            List<Paciente> pacientes = seedPacientes(zonas);
            List<Enfermedad> enfermedades = seedEnfermedades();
            seedDatosAmbientales(zonas);
            seedDiagnosticos(pacientes, enfermedades, zonas);
            seedAlertas(enfermedades, zonas);
            seedCalidadAgua();
            seedReportesResiduos(zonas);
        };
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() > 0) return;
        usuarioRepository.save(Usuario.builder()
                .nombre("Administrador").email("admin@hri.com")
                .password(passwordEncoder.encode("admin123"))
                .rol(Rol.ADMIN).activo(true).build());
        usuarioRepository.save(Usuario.builder()
                .nombre("Dra. Ana Torres").email("medico@hri.com")
                .password(passwordEncoder.encode("medico123"))
                .rol(Rol.MEDICO).activo(true).build());
        usuarioRepository.save(Usuario.builder()
                .nombre("Luis Quispe").email("supervisor@hri.com")
                .password(passwordEncoder.encode("supervisor123"))
                .rol(Rol.SUPERVISOR).activo(true).build());
        log.info("Usuarios de ejemplo creados (admin@hri.com / admin123)");
    }

    // Distritos de la region Ica, tal como se describe en el Capitulo 2 del informe
    private Map<String, Zona> seedZonas() {
        if (zonaRepository.count() > 0) {
            return zonaRepository.findAll().stream()
                    .collect(java.util.stream.Collectors.toMap(Zona::getNombre, z -> z));
        }
        Map<String, Zona> zonas = new java.util.LinkedHashMap<>();
        zonas.put("Ica", zonaRepository.save(Zona.builder()
                .nombre("Ica").latitud(-14.0678).longitud(-75.7286).nivelRiesgo(NivelAlerta.BAJO).build()));
        zonas.put("Subtanjalla", zonaRepository.save(Zona.builder()
                .nombre("Subtanjalla").latitud(-14.0339).longitud(-75.7519).nivelRiesgo(NivelAlerta.BAJO).build()));
        zonas.put("Parcona", zonaRepository.save(Zona.builder()
                .nombre("Parcona").latitud(-14.0453).longitud(-75.6883).nivelRiesgo(NivelAlerta.BAJO).build()));
        zonas.put("La Tinguina", zonaRepository.save(Zona.builder()
                .nombre("La Tinguina").latitud(-14.0344).longitud(-75.7089).nivelRiesgo(NivelAlerta.BAJO).build()));
        zonas.put("Los Aquijes", zonaRepository.save(Zona.builder()
                .nombre("Los Aquijes").latitud(-14.1167).longitud(-75.6833).nivelRiesgo(NivelAlerta.BAJO).build()));
        log.info("Zonas de la region Ica creadas");
        return zonas;
    }

    private List<Paciente> seedPacientes(Map<String, Zona> zonas) {
        if (pacienteRepository.count() > 0) return pacienteRepository.findAll();
        List<Paciente> pacientes = List.of(
                paciente("Juan", "Perez", "10000001", 1985, 4, 12, "M", "Subtanjalla", zonas),
                paciente("Maria", "Lopez", "10000002", 1992, 9, 3, "F", "Subtanjalla", zonas),
                paciente("Carlos", "Ramirez", "10000003", 1978, 1, 22, "M", "Subtanjalla", zonas),
                paciente("Rosa", "Flores", "10000004", 1965, 6, 30, "F", "Subtanjalla", zonas),
                paciente("Jorge", "Huaman", "10000005", 1990, 11, 15, "M", "Subtanjalla", zonas),
                paciente("Elena", "Chavez", "10000006", 1988, 2, 8, "F", "Subtanjalla", zonas),
                paciente("Pedro", "Rojas", "10000007", 1975, 7, 19, "M", "Ica", zonas),
                paciente("Lucia", "Vargas", "10000008", 1995, 3, 27, "F", "Parcona", zonas),
                paciente("Miguel", "Torres", "10000009", 1982, 10, 5, "M", "La Tinguina", zonas)
        );
        pacientes.forEach(pacienteRepository::save);
        log.info("Pacientes de ejemplo creados en distritos de Ica");
        return pacientes;
    }

    private Paciente paciente(String nombres, String apellidos, String documento,
                              int anio, int mes, int dia, String sexo, String distrito,
                              Map<String, Zona> zonas) {
        Zona zona = zonas.get(distrito);
        return Paciente.builder()
                .nombres(nombres).apellidos(apellidos).documento(documento)
                .fechaNacimiento(LocalDate.of(anio, mes, dia)).sexo(sexo)
                .direccion("Distrito de " + distrito).telefono("9561" + documento.substring(5))
                .latitud(zona.getLatitud()).longitud(zona.getLongitud())
                .zona(zona).build();
    }

    private List<Enfermedad> seedEnfermedades() {
        if (enfermedadRepository.count() > 0) return enfermedadRepository.findAll();
        Enfermedad asma = enfermedadRepository.save(Enfermedad.builder()
                .nombre("Asma").codigoCie10("J45")
                .descripcion("Enfermedad respiratoria cronica agravada por material particulado")
                .contaminanteAsociado("PM2.5").build());
        Enfermedad epoc = enfermedadRepository.save(Enfermedad.builder()
                .nombre("EPOC").codigoCie10("J44")
                .descripcion("Enfermedad pulmonar obstructiva cronica")
                .contaminanteAsociado("PM10").build());
        Enfermedad bronquitis = enfermedadRepository.save(Enfermedad.builder()
                .nombre("Bronquitis aguda").codigoCie10("J20")
                .descripcion("Inflamacion de bronquios asociada a contaminacion del aire")
                .contaminanteAsociado("NO2").build());
        enfermedadRepository.save(Enfermedad.builder()
                .nombre("Dermatitis por agroquimicos").codigoCie10("L25")
                .descripcion("Afeccion dermica asociada a exposicion a pesticidas y fertilizantes")
                .contaminanteAsociado("Agroquimicos").build());
        enfermedadRepository.save(Enfermedad.builder()
                .nombre("Gastroenteritis").codigoCie10("A09")
                .descripcion("Enfermedad gastrointestinal asociada a agua contaminada")
                .contaminanteAsociado("Aguas servidas").build());
        log.info("Catalogo de enfermedades creado");
        return List.of(asma, epoc, bronquitis);
    }

    private void seedDatosAmbientales(Map<String, Zona> zonas) {
        if (datoAmbientalRepository.count() > 0) return;
        LocalDateTime ahora = LocalDateTime.now();
        // Subtanjalla y Parcona concentran mayor actividad agroindustrial: peor calidad de aire
        datoAmbientalRepository.save(dato(zonas.get("Ica"), ahora.minusHours(1),
                18.0, 35.0, 40.0, 25.0, 45, "Estacion Ica Centro"));
        datoAmbientalRepository.save(dato(zonas.get("Subtanjalla"), ahora.minusHours(2),
                95.0, 140.0, 120.0, 110.0, 160, "Estacion Subtanjalla"));
        datoAmbientalRepository.save(dato(zonas.get("Subtanjalla"), ahora.minusHours(26),
                88.0, 130.0, 110.0, 100.0, 150, "Estacion Subtanjalla"));
        datoAmbientalRepository.save(dato(zonas.get("Parcona"), ahora.minusHours(3),
                60.0, 95.0, 75.0, 65.0, 100, "Estacion Parcona"));
        datoAmbientalRepository.save(dato(zonas.get("La Tinguina"), ahora.minusHours(4),
                30.0, 55.0, 50.0, 40.0, 60, "Estacion La Tinguina"));
        datoAmbientalRepository.save(dato(zonas.get("Los Aquijes"), ahora.minusHours(5),
                22.0, 40.0, 42.0, 28.0, 50, "Estacion Los Aquijes"));
        log.info("Datos ambientales de ejemplo creados por zona");
    }

    private DatoAmbiental dato(Zona zona, LocalDateTime fecha,
                               double pm25, double pm10, double o3, double no2,
                               int ica, String fuente) {
        return DatoAmbiental.builder()
                .latitud(zona.getLatitud()).longitud(zona.getLongitud())
                .zona(zona).fechaMedicion(fecha)
                .pm25(pm25).pm10(pm10).o3(o3).no2(no2).co2(400.0)
                .indiceCalidadAire(ica).fuente(fuente).build();
    }

    // Concentra 6 diagnosticos respiratorios en Subtanjalla dentro de los ultimos 7 dias,
    // superando el umbral de la regla de negocio RN02 (>5 casos) para demostrar la
    // generacion automatica de alertas al ejecutar el Servicio de Analisis.
    private void seedDiagnosticos(List<Paciente> pacientes, List<Enfermedad> enfermedades, Map<String, Zona> zonas) {
        if (diagnosticoRepository.count() > 0) return;
        Enfermedad asma = enfermedades.get(0);
        Zona subtanjalla = zonas.get("Subtanjalla");
        LocalDate hoy = LocalDate.now();

        for (int i = 0; i < Math.min(6, pacientes.size()); i++) {
            Paciente p = pacientes.get(i);
            if (p.getZona() != null && p.getZona().getNombre().equals("Subtanjalla")) {
                diagnosticoRepository.save(Diagnostico.builder()
                        .paciente(p).enfermedad(asma).zona(subtanjalla)
                        .fechaDiagnostico(hoy.minusDays(i)).build());
            }
        }
        log.info("Diagnosticos de ejemplo creados (concentrados en Subtanjalla)");
    }

    private void seedAlertas(List<Enfermedad> enfermedades, Map<String, Zona> zonas) {
        if (alertaRepository.count() > 0) return;
        Enfermedad asma = enfermedades.isEmpty() ? null : enfermedades.get(0);
        Zona subtanjalla = zonas.get("Subtanjalla");
        alertaRepository.save(Alerta.builder()
                .titulo("Calidad del aire mala en Subtanjalla")
                .descripcion("ICA 160: riesgo para grupos sensibles (asma, EPOC)")
                .nivel(NivelAlerta.ALTO).latitud(subtanjalla.getLatitud()).longitud(subtanjalla.getLongitud())
                .zona(subtanjalla).enfermedad(asma).atendida(false).build());
        log.info("Alertas de ejemplo creadas. Ejecuta POST /api/analisis/ejecutar para generar alertas automaticas.");
    }

    // Registros de ejemplo del conector ANA (calidad de agua por cuenca)
    private void seedCalidadAgua() {
        if (calidadAguaRepository.count() > 0) return;
        calidadAguaRepository.save(CalidadAgua.builder()
                .cuenca("Rio Ica").ph(6.8).turbidezNtu(35.0).coliformesFecales(120.0)
                .fechaMuestreo(LocalDate.now().minusDays(3)).fuente("ANA (ejemplo)").build());
        calidadAguaRepository.save(CalidadAgua.builder()
                .cuenca("Canal La Achirana").ph(7.1).turbidezNtu(18.0).coliformesFecales(40.0)
                .fechaMuestreo(LocalDate.now().minusDays(5)).fuente("ANA (ejemplo)").build());
        log.info("Registros de calidad de agua (ANA) de ejemplo creados");
    }

    // Reporte de ejemplo del conector Municipalidad (zonas de riesgo por residuos solidos)
    private void seedReportesResiduos(Map<String, Zona> zonas) {
        if (reporteResiduosRepository.count() > 0) return;
        reporteResiduosRepository.save(ReporteResiduos.builder()
                .zona(zonas.get("Subtanjalla"))
                .descripcion("Acumulacion de residuos solidos cerca del canal de regadio")
                .nivelRiesgo(NivelAlerta.MEDIO)
                .fechaReporte(LocalDate.now().minusDays(2))
                .reportadoPor("Municipalidad de Ica").build());
        log.info("Reporte de residuos solidos (Municipalidad) de ejemplo creado");
    }
}
