package com.example.AppBackend.dto;

import com.example.AppBackend.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Provision a local AppBackend user record (called by Auth API after registration)")
public record CreateUserRequest(
        @Schema(description = "User UUID from Auth API (JWT sub claim)", example = "a7f3c2e1-9b4d-4a1e-8c6f-2d5e9a1b3c7d")
        @NotNull UUID userId,

        @Schema(description = "Application role assigned in this service")
        @NotNull Role role
) {
}
