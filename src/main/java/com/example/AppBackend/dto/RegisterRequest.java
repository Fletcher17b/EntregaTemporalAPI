package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Register a new user by forwarding credentials to the Auth API")
public record RegisterRequest(
        @Schema(description = "Email used by Auth API", example = "user@example.com")
        @NotBlank
        @Email
        String email,

        @Schema(description = "Password used by Auth API", example = "P@ssw0rd")
        @NotBlank
        String password,

        @Schema(description = "Local username for AppBackend", example = "mario")
        @NotBlank
        String username
) {
}
