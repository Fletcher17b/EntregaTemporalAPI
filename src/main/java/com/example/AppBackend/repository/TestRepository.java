package com.example.AppBackend.repository;

import com.example.AppBackend.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, String> {

    @Query("SELECT DISTINCT t FROM Test t LEFT JOIN FETCH t.questions WHERE t.id = :testId")
    Optional<Test> findByIdWithQuestions(@Param("testId") String testId);
}
