package com.example.AppBackend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "test_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestSession {

    @Id
    private String id;
    private String userId;
    private String testId;
    private Instant startedAt;
    private Instant completedAt;
}
