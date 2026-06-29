package com.example.AppBackend.util;

public final class EntityIds {

    public static final String SEPARATOR = ".";

    private EntityIds() {
    }

    public static String questionId(String testId, int questionIndex) {
        return testId + SEPARATOR + "q" + questionIndex;
    }

    public static String optionId(String testId, int questionIndex, int optionIndex) {
        return questionId(testId, questionIndex) + SEPARATOR + "o" + optionIndex;
    }
}
