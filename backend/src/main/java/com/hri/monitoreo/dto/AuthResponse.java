package com.hri.monitoreo.dto;

public record AuthResponse(
        String token,
        String email,
        String rol
) {}
