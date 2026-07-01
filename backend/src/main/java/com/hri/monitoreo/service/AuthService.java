package com.hri.monitoreo.service;

import com.hri.monitoreo.dto.AuthResponse;
import com.hri.monitoreo.dto.LoginRequest;
import com.hri.monitoreo.dto.RegisterRequest;
import com.hri.monitoreo.entity.Usuario;
import com.hri.monitoreo.repository.UsuarioRepository;
import com.hri.monitoreo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rol(request.rol())
                .activo(true)
                .build();
        usuarioRepository.save(usuario);

        return construirRespuesta(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        return construirRespuesta(usuario);
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        UserDetails userDetails = new User(
                usuario.getEmail(), usuario.getPassword(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().name())));
        String token = jwtService.generarToken(userDetails);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRol().name());
    }
}
