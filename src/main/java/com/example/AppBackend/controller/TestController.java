package com.example.AppBackend.controller;

import com.example.AppBackend.dto.SubmitTestRequest;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.TestResult;
import com.example.AppBackend.service.TestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/{testId}")
    public Test getTest(@PathVariable String testId) {
        return testService.getTestById(testId);
    }

    @PostMapping("/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public TestResult submitTest(@Valid @RequestBody SubmitTestRequest request) {
        return testService.submitTest(request);
    }
}
