package com.example.AppBackend.dto;

import com.example.AppBackend.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Created AppBackend user details")
public record RegistrationResponse(
        @Schema(description = "Local AppBackend user id", example = "a7f3c2e1-9b4d-4a1e-8c6f-2d5e9a1b3c7d")
        UUID userId,

        @Schema(description = "Local AppBackend username", example = "mario")
        String username,

        @Schema(description = "Role returned by Auth API")
        Role role
) {
}
