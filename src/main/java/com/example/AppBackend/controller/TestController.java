package com.example.AppBackend.controller;

import com.example.AppBackend.dto.CreateTestRequest;
import com.example.AppBackend.dto.SubmitTestRequest;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.TestResult;
import com.example.AppBackend.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
@Tag(name = "Tests", description = "Test template creation, retrieval, and submission endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Create test template",
            description = "Creates a new test. You provide the testId; question and option IDs are generated automatically as testId.q{n} and testId.q{n}.o{m}."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Test created"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid access token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Requires ADMIN role",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "A test with this id already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public Test createTest(@Valid @RequestBody CreateTestRequest request) {
        return testService.createTest(request);
    }

    @GetMapping("/{testId}")
    @Operation(
            summary = "Fetch test template",
            description = "Returns the static test structure including questions and answer options."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test found"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid access token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public Test getTest(
            @Parameter(description = "Unique test identifier", example = "1")
            @PathVariable String testId
    ) {
        return testService.getTestById(testId);
    }

    @PostMapping("/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Submit completed test",
            description = "Persists a test session, scores the submitted answers, and returns the computed result."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Submission scored and stored"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid access token",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "userId does not match authenticated user",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public TestResult submitTest(@Valid @RequestBody SubmitTestRequest request) {
        return testService.submitTest(request);
    }
}
