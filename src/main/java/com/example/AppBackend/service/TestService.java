package com.example.AppBackend.service;

import com.example.AppBackend.dto.CreateOptionRequest;
import com.example.AppBackend.dto.CreateQuestionRequest;
import com.example.AppBackend.dto.CreateTestRequest;
import com.example.AppBackend.dto.SubmitTestRequest;
import com.example.AppBackend.dto.UserAnswer;
import com.example.AppBackend.entity.Option;
import com.example.AppBackend.entity.Question;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.TestResult;
import com.example.AppBackend.entity.TestSession;
import com.example.AppBackend.exception.ResourceConflictException;
import com.example.AppBackend.exception.ResourceNotFoundException;
import com.example.AppBackend.repository.TestRepository;
import com.example.AppBackend.repository.TestResultRepository;
import com.example.AppBackend.repository.TestSessionRepository;
import com.example.AppBackend.security.CurrentUserService;
import com.example.AppBackend.util.EntityIds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;
    private final CurrentUserService currentUserService;

    public TestService(TestRepository testRepository,
                       TestSessionRepository testSessionRepository,
                       TestResultRepository testResultRepository,
                       CurrentUserService currentUserService) {
        this.testRepository = testRepository;
        this.testSessionRepository = testSessionRepository;
        this.testResultRepository = testResultRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public Test getTestById(String testId) {
        return loadTestBlueprint(testId);
    }

    @Transactional
    public Test createTest(CreateTestRequest request) {
        String testId = request.testId();
        if (testRepository.existsById(testId)) {
            throw new ResourceConflictException("Test already exists with id: " + testId);
        }

        List<Question> questions = new ArrayList<>();

        for (int questionIndex = 0; questionIndex < request.questions().size(); questionIndex++) {
            CreateQuestionRequest questionRequest = request.questions().get(questionIndex);
            int questionNumber = questionIndex + 1;
            String questionId = EntityIds.questionId(testId, questionNumber);
            List<Option> options = new ArrayList<>();

            for (int optionIndex = 0; optionIndex < questionRequest.options().size(); optionIndex++) {
                CreateOptionRequest optionRequest = questionRequest.options().get(optionIndex);
                options.add(new Option(
                        EntityIds.optionId(testId, questionNumber, optionIndex + 1),
                        optionRequest.text(),
                        optionRequest.weight()
                ));
            }

            questions.add(new Question(questionId, questionRequest.text(), options));
        }

        Test test = new Test(testId, request.version(), request.title(), questions);
        Test saved = testRepository.save(test);
        return loadTestBlueprint(saved.getId());
    }

    @Transactional
    public TestResult submitTest(SubmitTestRequest request) {
        currentUserService.requireMatchingUserId(request.userId());

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
