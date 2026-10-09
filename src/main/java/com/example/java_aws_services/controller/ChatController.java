package com.example.java_aws_services.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.ResponseEntity;

import com.example.java_aws_services.service.RagService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final RagService ragService;

    public ChatController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping
    public String getDefaultChat() {
        return "Hey, explore multiple chat options!";
    }

    @GetMapping("/rag")
    public String getRagChat() {
        return "Hey, this route expects a POST request with a question!";
    }

    @PostMapping("/rag")
    public ResponseEntity<String> getRagChat(@RequestBody RagRequest request) {

        String response = ragService.askQuestion(request.question());

        return ResponseEntity.ok(response);
    }

    public record RagRequest(String question) {
    }
}