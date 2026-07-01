package com.hri.monitoreo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas de seguridad del servicio JWT: generacion, extraccion y expiracion de tokens
class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void configurar() {
        ReflectionTestUtils.setField(jwtService, "secret",
                "dGVzdC1zZWNyZXQtcGFyYS1wcnVlYmFzLXVuaXRhcmlhcy1uby11c2FyLWVuLXByb2R1Y2Npb24=");
    }

    private UserDetails usuarioDePrueba() {
        return new User("medico@hri.com", "hash",
                List.of(new SimpleGrantedAuthority("ROLE_MEDICO")));
    }

    @Test
    void generaYValidaUnTokenCorrectamente() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
        UserDetails usuario = usuarioDePrueba();

        String token = jwtService.generarToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extraerUsername(token)).isEqualTo("medico@hri.com");
        assertThat(jwtService.esValido(token, usuario)).isTrue();
    }

    @Test
    void unTokenExpiradoNoEsValido() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expirationMs", 1L);
        UserDetails usuario = usuarioDePrueba();

        String token = jwtService.generarToken(usuario);
        Thread.sleep(50);

        assertThat(jwtService.esValido(token, usuario)).isFalse();
    }

    @Test
    void unTokenNoCorrespondeAOtroUsuario() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
        String token = jwtService.generarToken(usuarioDePrueba());

        UserDetails otroUsuario = new User("admin@hri.com", "hash",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        assertThat(jwtService.esValido(token, otroUsuario)).isFalse();
    }
}
