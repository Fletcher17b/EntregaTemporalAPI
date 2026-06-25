package com.example.AppBackend.repository;

import com.example.AppBackend.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestResultRepository extends JpaRepository<TestResult, String> {
}
