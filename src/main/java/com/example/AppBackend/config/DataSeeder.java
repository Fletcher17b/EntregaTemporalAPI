package com.example.AppBackend.config;

import com.example.AppBackend.entity.Option;
import com.example.AppBackend.entity.Question;
import com.example.AppBackend.entity.Role;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.User;
import com.example.AppBackend.repository.TestRepository;
import com.example.AppBackend.repository.UserRepository;
import com.example.AppBackend.util.EntityIds;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final UUID ADMIN_USER_ID = UUID.fromString("a7f3c2e1-9b4d-4a1e-8c6f-2d5e9a1b3c7d");
    private static final UUID USER_ONE_ID = UUID.fromString("b2e8d4f0-1c3a-4f7e-9d2b-6a8c0e4f1b5d");
    private static final UUID USER_TWO_ID = UUID.fromString("c9d1e6a2-5f8b-4c3d-a7e1-0b4f6d8a2c9e");

    private final UserRepository userRepository;
    private final TestRepository testRepository;

    public DataSeeder(UserRepository userRepository, TestRepository testRepository) {
        this.userRepository = userRepository;
        this.testRepository = testRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0 || testRepository.count() > 0) {
            return;
        }
        User admin = new User();
        admin.setId(ADMIN_USER_ID);
        admin.setUsername("admin");
        admin.setRole(Role.ADMIN);
        
        User user1 = new User();
        user1.setId(USER_ONE_ID);
        user1.setUsername("user1");
        user1.setRole(Role.USER);
        
        User user2 = new User();
        user2.setId(USER_TWO_ID);
        user2.setUsername("user2");
        user2.setRole(Role.USER);
        
        userRepository.saveAll(List.of(admin, user1, user2));

        testRepository.saveAll(List.of(
                buildWellnessTest(),
                buildWorkStyleTest(),
                buildLearningTest()
        ));
    }

    private Test buildWellnessTest() {
        String testId = "1";
        return new Test(
                testId,
                "2.1",
                "Daily Wellness Pulse",
                List.of(
                        question(testId, 1,
                                "Se acerca la fecha límite de un proyecto final y tu computadora falla.",
                                List.of(
                                        option(testId, 1, 1, "Mantengo la calma, busco otra PC y aviso al profesor.", 5),
                                        option(testId, 1, 2, "Me frustro mucho pero intento resolverlo después de un rato.", 3),
                                        option(testId, 1, 3, "Entro en pánico y abandono el proyecto.", 1)
                                )
                        ),
                        question(testId, 2,
                                "El profesor te hace una corrección muy dura frente a la clase.",
                                List.of(
                                        option(testId, 2, 1, "Acepto la crítica constructiva para mejorar mi trabajo.", 5),
                                        option(testId, 2, 2, "Me siento mal, pero trato de no demostrarlo.", 3),
                                        option(testId, 2, 3, "Me enojo y le respondo a la defensiva.", 1)
                                )
                        ),
                        question(testId, 3,
                                "Tienes que elegir entre un examen fácil seguro o un proyecto difícil que enseña más.",
                                List.of(
                                        option(testId, 3, 1, "Elijo el proyecto difícil para aprender más.", 5),
                                        option(testId, 3, 2, "Lo dudo mucho, pero termino eligiendo el proyecto.", 3),
                                        option(testId, 3, 3, "Elijo el examen fácil para asegurar la nota sin esfuerzo.", 1)
                                )
                        )
                )
        );
    }

    private Test buildWorkStyleTest() {
        String testId = "2";
        return new Test(
                testId,
                "1.0",
                "Remote Work Habits Survey",
                List.of(
                        question(testId, 1,
                                "When starting a new task, you usually:",
                                List.of(
                                        option(testId, 1, 1, "Outline steps before touching the keyboard", 1),
                                        option(testId, 1, 2, "Prototype quickly and refine later", 3),
                                        option(testId, 1, 3, "Wait for someone else to kick things off", 0)
                                )
                        ),
                        question(testId, 2,
                                "Your ideal collaboration rhythm is:",
                                List.of(
                                        option(testId, 2, 1, "Async docs with occasional sync calls", 2),
                                        option(testId, 2, 2, "Pairing sessions most of the day", 3),
                                        option(testId, 2, 3, "Solo deep work with zero interruptions", 1)
                                )
                        )
                )
        );
    }

    private Test buildLearningTest() {
        String testId = "3";
        return new Test(
                testId,
                "1.2",
                "Learning Style Snapshot",
                List.of(
                        question(testId, 1,
                                "You remember new concepts best when you:",
                                List.of(
                                        option(testId, 1, 1, "Read detailed explanations", 2),
                                        option(testId, 1, 2, "Watch someone demonstrate it", 3),
                                        option(testId, 1, 3, "Try it hands-on immediately", 4)
                                )
                        ),
                        question(testId, 2,
                                "After making a mistake, you prefer to:",
                                List.of(
                                        option(testId, 2, 1, "Re-read the theory behind it", 1),
                                        option(testId, 2, 2, "Ask a peer what they would do", 2),
                                        option(testId, 2, 3, "Experiment with a different approach", 3)
                                )
                        ),
                        question(testId, 3,
                                "How do you feel about timed quizzes?",
                                List.of(
                                        option(testId, 3, 1, "They sharpen my focus", 3),
                                        option(testId, 3, 2, "Neutral — depends on the topic", 2),
                                        option(testId, 3, 3, "They make me anxious", 0)
                                )
                        )
                )
        );
    }

    private Question question(String testId, int questionNumber, String text, List<Option> options) {
        return new Question(EntityIds.questionId(testId, questionNumber), text, options);
    }

    private Option option(String testId, int questionNumber, int optionNumber, String text, int weight) {
        return new Option(EntityIds.optionId(testId, questionNumber, optionNumber), text, weight);
    }
}
