package com.resumebinder.model;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GeminiResponse {
    private ResumeData resumeData;
    private int atsScore;
    private Map<String, List<String>> keywordMatchAnalysis;
    private List<String> improvementSuggestions;
}
