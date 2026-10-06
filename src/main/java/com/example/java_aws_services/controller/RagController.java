package com.example.java_aws_services.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.java_aws_services.service.RagService;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody RagRequest request) {

        String response = ragService.askQuestion(request.question());

        return ResponseEntity.ok(response);
    }

    public record RagRequest(String question) {
    }
}