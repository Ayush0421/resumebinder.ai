package com.resumebinder.controller;

import com.resumebinder.model.GeminiResponse;
import com.resumebinder.model.ResumeRequest;
import com.resumebinder.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private TemplateEngine templateEngine;

    @PostMapping("/generate")
    public ResponseEntity<?> generateResume(@RequestBody ResumeRequest request) {
        try {
            GeminiResponse response = geminiService.generateResume(request.getJd(), request.getRawResume());
            
            // Render Thymeleaf HTML
            Context context = new Context();
            context.setVariable("data", response.getResumeData());
            String html = templateEngine.process("resume", context);

            // Construct Response
            Map<String, Object> result = new HashMap<>();
            result.put("html", html);
            result.put("atsScore", response.getAtsScore());
            result.put("keywordMatchAnalysis", response.getKeywordMatchAnalysis());
            result.put("improvementSuggestions", response.getImprovementSuggestions());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PostMapping("/extract-url")
    public ResponseEntity<?> extractJobDescription(@RequestBody Map<String, String> payload) {
        try {
            String url = payload.get("url");
            if (url == null || url.isEmpty()) {
                throw new IllegalArgumentException("URL is required");
            }
            String jdText = geminiService.extractJobDescriptionFromUrl(url);
            Map<String, String> response = new HashMap<>();
            response.put("jdText", jdText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
