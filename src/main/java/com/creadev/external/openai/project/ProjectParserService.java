package com.creadev.external.openai.project;

public interface ProjectParserService {
    /**
     * Parse a freeform project request and return the structured output.
     *
     * @param request the user's project request prompt
     * @return the parsed project request as structured formatted text
     */
    String parse(String request);
} 