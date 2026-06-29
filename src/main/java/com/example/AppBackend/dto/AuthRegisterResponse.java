package com.example.AppBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRegisterResponse(
        @JsonProperty("user_id")
        String userId,
        @JsonProperty("role")
        String role
) {
}
