package com.example.AppBackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "A question with between 1 and 5 answer options")
public record CreateQuestionRequest(
        @Schema(description = "Question prompt text", example = "How do you handle stress?")
        @NotBlank String text,

        @Schema(description = "Answer choices (minimum 1, maximum 5)")
        @NotEmpty
        @Size(min = 1, max = 5)
        List<@Valid CreateOptionRequest> options
) {
}
