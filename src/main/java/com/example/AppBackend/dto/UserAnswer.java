package com.example.AppBackend.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAnswer(
        @NotBlank String questionId,
        @NotBlank String optionId
) {
}
