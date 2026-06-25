package com.example.AppBackend.config;

import com.example.AppBackend.entity.Option;
import com.example.AppBackend.entity.Question;
import com.example.AppBackend.entity.Test;
import com.example.AppBackend.entity.User;
import com.example.AppBackend.repository.TestRepository;
import com.example.AppBackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

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

        userRepository.saveAll(List.of(
                new User("a7f3c2e1-9b4d-4a1e-8c6f-2d5e9a1b3c7d"),
                new User("b2e8d4f0-1c3a-4f7e-9d2b-6a8c0e4f1b5d"),
                new User("c9d1e6a2-5f8b-4c3d-a7e1-0b4f6d8a2c9e")
        ));

        testRepository.saveAll(List.of(
                buildWellnessTest(),
                buildWorkStyleTest(),
                buildLearningTest()
        ));
    }

    private Test buildWellnessTest() {
        return new Test(
                "1",
                "2.1",
                "Daily Wellness Pulse",
                List.of(
                        new Question(
                            "1-q1",
                            "Se acerca la fecha límite de un proyecto final y tu computadora falla.",
                            List.of(
                                    new Option("1-q1-o1", "Mantengo la calma, busco otra PC y aviso al profesor.", 5),
                                    new Option("1-q1-o2", "Me frustro mucho pero intento resolverlo después de un rato.", 3),
                                    new Option("1-q1-o3", "Entro en pánico y abandono el proyecto.", 1)
                            )
                        ),
                        new Question(
                            "1-q2",
                            "El profesor te hace una corrección muy dura frente a la clase.",
                            List.of(
                                    new Option("1-q2-o1", "Acepto la crítica constructiva para mejorar mi trabajo.", 5),
                                    new Option("1-q2-o2", "Me siento mal, pero trato de no demostrarlo.", 3),
                                    new Option("1-q2-o3", "Me enojo y le respondo a la defensiva.", 1)
                            )
                        ),
                        new Question(
                            "1-q3",
                            "Tienes que elegir entre un examen fácil seguro o un proyecto difícil que enseña más.",
                            List.of(
                                    new Option("1-q3-o1", "Elijo el proyecto difícil para aprender más.", 5),
                                    new Option("1-q3-o2", "Lo dudo mucho, pero termino eligiendo el proyecto.", 3),
                                    new Option("1-q3-o3", "Elijo el examen fácil para asegurar la nota sin esfuerzo.", 1)
                            )
                        )
                )
        );
    }

    private Test buildWorkStyleTest() {
        return new Test(
                "2",
                "1.0",
                "Remote Work Habits Survey",
                List.of(
                        new Question(
                                "2-q1",
                                "When starting a new task, you usually:",
                                List.of(
                                        new Option("2-q1-o1", "Outline steps before touching the keyboard", 1),
                                        new Option("2-q1-o2", "Prototype quickly and refine later", 3),
                                        new Option("2-q1-o3", "Wait for someone else to kick things off", 0)
                                )
                        ),
                        new Question(
                                "2-q2",
                                "Your ideal collaboration rhythm is:",
                                List.of(
                                        new Option("2-q2-o1", "Async docs with occasional sync calls", 2),
                                        new Option("2-q2-o2", "Pairing sessions most of the day", 3),
                                        new Option("2-q2-o3", "Solo deep work with zero interruptions", 1)
                                )
                        )
                )
        );
    }

    private Test buildLearningTest() {
        return new Test(
                "3",
                "1.2",
                "Learning Style Snapshot",
                List.of(
                        new Question(
                                "3-q1",
                                "You remember new concepts best when you:",
                                List.of(
                                        new Option("3-q1-o1", "Read detailed explanations", 2),
                                        new Option("3-q1-o2", "Watch someone demonstrate it", 3),
                                        new Option("3-q1-o3", "Try it hands-on immediately", 4)
                                )
                        ),
                        new Question(
                                "3-q2",
                                "After making a mistake, you prefer to:",
                                List.of(
                                        new Option("3-q2-o1", "Re-read the theory behind it", 1),
                                        new Option("3-q2-o2", "Ask a peer what they would do", 2),
                                        new Option("3-q2-o3", "Experiment with a different approach", 3)
                                )
                        ),
                        new Question(
                                "3-q3",
                                "How do you feel about timed quizzes?",
                                List.of(
                                        new Option("3-q3-o1", "They sharpen my focus", 3),
                                        new Option("3-q3-o2", "Neutral — depends on the topic", 2),
                                        new Option("3-q3-o3", "They make me anxious", 0)
                                )
                        )
                )
        );
    }
}
