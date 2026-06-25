package com.example.AppBackend.repository;

import com.example.AppBackend.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestSessionRepository extends JpaRepository<TestSession, String> {
}
