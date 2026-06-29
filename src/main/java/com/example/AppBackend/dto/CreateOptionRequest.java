package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "An answer option with a scoring weight")
public record CreateOptionRequest(
        @Schema(description = "Display text for the option", example = "Strongly agree")
        @NotBlank String text,

        @Schema(description = "Score weight applied when this option is selected", example = "3")
        int weight
) {
}
