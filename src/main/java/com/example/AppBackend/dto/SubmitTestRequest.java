package com.example.AppBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record SubmitTestRequest(
        @NotBlank String testId,
        @NotBlank String userId,
        @NotEmpty List<@Valid UserAnswer> answers,
        @NotNull Instant startedAt,
        @NotNull Instant completedAt
) {
}
