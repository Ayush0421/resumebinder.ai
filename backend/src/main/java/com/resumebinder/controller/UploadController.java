package com.resumebinder.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class UploadController {

    @PostMapping("/extract-pdf")
    public ResponseEntity<?> extractPdf(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty.");
            }
            if (!"application/pdf".equals(file.getContentType())) {
                throw new IllegalArgumentException("File must be a PDF.");
            }

            try (InputStream inputStream = file.getInputStream();
                 PDDocument document = PDDocument.load(inputStream)) {
                
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);

                Map<String, String> result = new HashMap<>();
                result.put("rawText", text);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to parse PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
