package com.example.AppBackend.controller;

import com.example.AppBackend.dto.CreateUserRequest;
import com.example.AppBackend.entity.User;
import com.example.AppBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@Tag(name = "Internal", description = "Service-to-service endpoints (Auth API only)")
@SecurityRequirements
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Provision user",
            description = "Creates a local user record with role. Called by the Auth API over HTTP after registration. "
                    + "Requires the X-Internal-Api-Key header."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User provisioned"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid internal API key",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public User provisionUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.provisionUser(request);
    }
}
