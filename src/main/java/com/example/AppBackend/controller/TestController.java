package com.example.AppBackend.controller;

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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
@Tag(name = "Tests", description = "Test template retrieval and submission endpoints")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/{testId}")
    @Operation(
            summary = "Fetch test template",
            description = "Returns the static test structure including questions and answer options."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test found"),
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
                    responseCode = "404",
                    description = "Test not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    public TestResult submitTest(@Valid @RequestBody SubmitTestRequest request) {
        return testService.submitTest(request);
    }
}
