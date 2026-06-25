package com.example.AppBackend.service;

import com.example.AppBackend.dto.SubmitTestRequest;
import com.example.AppBackend.dto.UserAnswer;
import com.example.AppBackend.entity.Option;
import com.example.AppBackend.entity.Question;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.TestResult;
import com.example.AppBackend.entity.TestSession;
import com.example.AppBackend.exception.ResourceNotFoundException;
import com.example.AppBackend.repository.TestRepository;
import com.example.AppBackend.repository.TestResultRepository;
import com.example.AppBackend.repository.TestSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;

    public TestService(TestRepository testRepository,
                       TestSessionRepository testSessionRepository,
                       TestResultRepository testResultRepository) {
        this.testRepository = testRepository;
        this.testSessionRepository = testSessionRepository;
        this.testResultRepository = testResultRepository;
    }

    @Transactional(readOnly = true)
    public Test getTestById(String testId) {
        return loadTestBlueprint(testId);
    }

    @Transactional
    public TestResult submitTest(SubmitTestRequest request) {
        Test test = loadTestBlueprint(request.testId());

        String sessionId = UUID.randomUUID().toString();
        TestSession session = new TestSession(
                sessionId,
                request.userId(),
                request.testId(),
                request.startedAt(),
                request.completedAt()
        );
        testSessionRepository.save(session);

        Map<String, Integer> optionWeights = buildOptionWeightIndex(test);
        Map<String, Integer> categoryScores = new HashMap<>();
        int totalScore = 0;

        for (UserAnswer answer : request.answers()) {
            String key = answerKey(answer.questionId(), answer.optionId());
            Integer weight = optionWeights.get(key);
            if (weight != null) {
                totalScore += weight;
                categoryScores.put(answer.questionId(), weight);
            }
        }

        String interpretation = totalScore < 5 ? "Low" : "High";

        TestResult result = new TestResult(
                UUID.randomUUID().toString(),
                sessionId,
                totalScore,
                interpretation,
                categoryScores
        );

        return testResultRepository.save(result);
    }

    private Test loadTestBlueprint(String testId) {
        Test test = testRepository.findByIdWithQuestions(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + testId));
        test.getQuestions().forEach(question -> question.getOptions().size());
        return test;
    }

    private Map<String, Integer> buildOptionWeightIndex(Test test) {
        Map<String, Integer> weights = new HashMap<>();
        for (Question question : test.getQuestions()) {
            for (Option option : question.getOptions()) {
                weights.put(answerKey(question.getId(), option.getId()), option.getWeight());
            }
        }
        return weights;
    }

    private String answerKey(String questionId, String optionId) {
        return questionId + ":" + optionId;
    }
}
