package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

@Schema(description = "Payload for submitting a completed test session")
public record SubmitTestRequest(
        @Schema(description = "ID of the test being submitted", example = "1")
        @NotBlank String testId,

        @Schema(description = "ID of the user submitting the test", example = "a7f3c2e1-9b4d-4a1e-8c6f-2d5e9a1b3c7d")
        @NotBlank String userId,

        @Schema(description = "Selected answers for each question")
        @NotEmpty List<@Valid UserAnswer> answers,

        @Schema(description = "When the user started the test", example = "2026-06-24T10:00:00Z")
        @NotNull Instant startedAt,

        @Schema(description = "When the user completed the test", example = "2026-06-24T10:05:00Z")
        @NotNull Instant completedAt
) {
}
