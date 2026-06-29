package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Payload for creating a new test template")
public record CreateTestRequest(
        @Schema(description = "Unique test identifier (questions and options are derived from this)", example = "wellness-check")
        @NotBlank String testId,

        @Schema(description = "Test version identifier", example = "1.0")
        @NotBlank String version,

        @Schema(description = "Human-readable test title", example = "Team Collaboration Survey")
        @NotBlank String title,

        @Schema(description = "Questions that make up the test (at least one required)")
        @NotEmpty List<@Valid CreateQuestionRequest> questions
) {
}
