package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "A single answer selection for a question")
public record UserAnswer(
        @Schema(description = "ID of the answered question", example = "1-q1")
        @NotBlank String questionId,

        @Schema(description = "ID of the selected option", example = "1-q1-o3")
        @NotBlank String optionId
) {
}
