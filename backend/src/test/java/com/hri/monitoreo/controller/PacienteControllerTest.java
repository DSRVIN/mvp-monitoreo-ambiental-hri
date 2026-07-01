package com.hri.monitoreo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Pruebas funcionales del Servicio de Pacientes (CP-01, CP-02 del Capitulo 9)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "MEDICO")
class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listarPacientesDevuelveUnaPaginaConLosDatosDeEjemplo() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void registrarPacienteConDatosValidosDevuelve201() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombres":"Ana","apellidos":"Quispe","documento":"77778888","sexo":"F"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombres").value("Ana"));
    }

    @Test
    void registrarPacienteSinNombresDevuelve400() throws Exception {
        mockMvc.perform(post("/api/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"apellidos":"Quispe","documento":"77778889"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPacienteInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/pacientes/99999"))
                .andExpect(status().isNotFound());
    }
}
