package com.example.AppBackend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestResult {

    @Id
    private String id;
    private String sessionId;
    private int totalScore;
    private String interpretation;

    @ElementCollection
    @CollectionTable(name = "result_category_scores", joinColumns = @JoinColumn(name = "result_id"))
    @MapKeyColumn(name = "category")
    @Column(name = "score")
    private Map<String, Integer> categoryScores = new HashMap<>();
}
