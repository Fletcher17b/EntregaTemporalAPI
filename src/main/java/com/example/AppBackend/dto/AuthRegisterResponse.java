package com.example.AppBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthRegisterResponse(
        @JsonProperty("userid")
        String userId,
        @JsonProperty("role")
        String role
) {
}
