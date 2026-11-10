package com.example.aiLearn.controller;


import com.example.aiLearn.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleRAGController {

    private final RagService ragService;

    public SimpleRAGController(RagService ragService) {
        this.ragService = ragService;
    }

    /**
     * Endpoint to chat with organizational data.
     * Example: GET http://localhost:8080/api/v1/ai/ask?question=What is the wifi password?
     */
    @GetMapping("/ask")
    public ResponseEntity<String> ask(@RequestParam(value = "question") String question) {
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Question query parameter cannot be empty.");
        }

        String aiResponse = ragService.askQuestion(question);
        return ResponseEntity.ok(aiResponse);
    }
}
