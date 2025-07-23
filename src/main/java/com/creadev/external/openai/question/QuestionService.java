package com.creadev.external.openai.question;

public interface QuestionService {
    /**
     * Send a user question to the OpenAI API and retrieve an answer.
     *
     * @param question the user's question
     * @return the AI-generated answer
     */
    String getAnswer(String question);
} 