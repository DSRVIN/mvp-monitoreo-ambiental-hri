package com.hri.monitoreo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Pruebas de seguridad: control de acceso por autenticacion y por rol (RN03, Capitulo 9.5)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accederSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MEDICO")
    void medicoNoPuedeAccederAGestionDeUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminSiPuedeAccederAGestionDeUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MEDICO")
    void medicoPuedeConsultarPacientes() throws Exception {
        mockMvc.perform(get("/api/pacientes"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerEsAccesibleSinAutenticacion() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }
}
