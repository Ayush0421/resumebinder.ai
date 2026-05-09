package com.resumebinder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebinder.model.GeminiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String chatUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResponse generateResume(String jd, String rawResume) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String systemPrompt = "You are an intelligent Resume Builder system that generates a highly ATS-optimized, ONE-PAGE resume tailored to a given Job Description (JD).\n" +
                "Maximize ATS score, keyword match, and recruiter readability. Your primary goal is to HACK the ATS by heavily weaving in required keywords.\n\n" +
                "STRICT CONSTRAINTS:\n" +
                "- DO NOT invent completely fake companies, roles, or degrees.\n" +
                "- MINDSET: Act as a rigorous Product-Based Company (PBC) HR recruiter. Look for high-impact keywords, concrete metrics, and strong presentation.\n" +
                "- WEAK WORDS: ABSOLUTELY DO NOT use weak words like 'familiarity', 'exposure', 'basic understanding', etc. Present all skills confidently.\n" +
                "- COMPANY CONTEXT: Tailor the resume strictly based on the provided Job Description and Target Company. If you lack context or details about the company, do not drastically alter the order or structure of the candidate's core experiences.\n" +
                "- FORMATTING: Use HTML <b> and </b> tags to bold the important keywords you inject or highlight. DO NOT use markdown (like **) for bolding. DO NOT bold individual skills in the Technologies section.\n" +
                "- LINKS: For the contactInfo field, format the Email and LinkedIn as actual HTML anchor tags (e.g., <a href=\"mailto:email@example.com\">email@example.com</a> | Phone | <a href=\"https://linkedin.com/in/username\">linkedin.com/in/username</a> | Location).\n" +
                "- ATS OPTIMIZATION IS PARAMOUNT: You MUST naturally integrate and weave the JD's missing keywords (like specific tools, skills, or frameworks) into the candidate's existing experience and projects where contextually plausible. DO NOT leave critical JD keywords unmatched if they can be blended into existing points.\n" +
                "- You MUST deeply ANALYZE the raw text against the JD. DO NOT just copy the input! REWRITE, REPHRASE, and REORDER bullet points to aggressively target the JD's exact terminology.\n" +
                "- TRUNCATE and REMOVE irrelevant experiences, older jobs, or weak bullet points so the entire output STRICTLY fits on a single page. Keep it under 450 words total.\n" +
                "- Output MUST be a structured JSON tightly matching this exact mapping:\n" +
                "{\n" +
                "  \"resumeData\": {\n" +
                "    \"name\": \"Full Name\",\n" +
                "    \"contactInfo\": \"Email | Phone | LinkedIn | Location\",\n" +
                "    \"experience\": [{ \"title\": \"Role\", \"company\": \"Company\", \"duration\": \"Date - Date\", \"bullets\": [\"Action...\", \"Result...\"] }],\n" +
                "    \"technologies\": [{ \"category\": \"Languages\", \"skills\": \"Java, Python\" }],\n" +
                "    \"projects\": [{ \"title\": \"Project Name\", \"duration\": \"Date\", \"bullets\": [\"Built X...\"] }],\n" +
                "    \"achievements\": [\"Award 1\", \"Award 2\"],\n" +
                "    \"education\": [{ \"institution\": \"Univ Name\", \"degree\": \"B.S.\", \"cgpa\": \"3.9\", \"duration\": \"2019-2023\" }]\n" +
                "  },\n" +
                "  \"atsScore\": 85,\n" +
                "  \"keywordMatchAnalysis\": {\n" +
                "    \"matchedKeywords\": [\"Java\", \"Spring Boot\"],\n" +
                "    \"missingKeywords\": [\"Kafka\"]\n" +
                "  },\n" +
                "  \"improvementSuggestions\": [\"Suggestion 1 based on missing keywords\"]\n" +
                "}\n" +
                "DO NOT wrap the response in markdown blocks like ```json. Return ONLY the raw JSON.";

        try {
            String userMessage = "Job Description:\\n" + jd + "\\n\\nCandidate Raw Resume Text:\\n" + rawResume;

            Map<String, Object> requestBody = new HashMap<>();
            
            // System instructions
            Map<String, Object> systemInstruction = new HashMap<>();
            Map<String, Object> systemParts = new HashMap<>();
            systemParts.put("text", systemPrompt);
            systemInstruction.put("parts", List.of(systemParts));
            requestBody.put("system_instruction", systemInstruction);

            // Generation config
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("response_mime_type", "application/json");
            requestBody.put("generationConfig", generationConfig);

            // User content
            Map<String, Object> userContent = new HashMap<>();
            userContent.put("role", "user");
            Map<String, Object> userPart = new HashMap<>();
            userPart.put("text", userMessage);
            userContent.put("parts", List.of(userPart));

            requestBody.put("contents", List.of(userContent));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            int maxRetries = 3;
            long backoffMillis = 2000;

            for (int attempt = 1; attempt <= maxRetries; attempt++) {
                try {
                    ResponseEntity<Map> response = restTemplate.postForEntity(chatUrl + "?key=" + apiKey, entity, Map.class);
                    Map<String, Object> body = response.getBody();

                    if (body != null && body.containsKey("candidates")) {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                        if (!candidates.isEmpty()) {
                            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            String jsonText = (String) parts.get(0).get("text");
                            return objectMapper.readValue(jsonText, GeminiResponse.class);
                        }
                    }
                    throw new RuntimeException("No valid response generated.");
                } catch (Exception e) {
                    boolean isRetryable = e.getMessage() != null && (e.getMessage().contains("503") || e.getMessage().contains("429") || e.getMessage().contains("Service Unavailable") || e.getMessage().contains("Too Many Requests"));
                    if (isRetryable && attempt < maxRetries) {
                        System.err.println("API overload detected (503/429). Retrying in " + backoffMillis + "ms... (Attempt " + attempt + " of " + maxRetries + ")");
                        try {
                            Thread.sleep(backoffMillis);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        backoffMillis *= 2; // Exponential backoff
                    } else {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
            throw new RuntimeException("API Call Failed after " + maxRetries + " attempts.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API Call Failed: " + e.getMessage());
        }
    }
}
