package com.example.java_aws_services.service;

import org.springframework.stereotype.Service;

import com.example.java_aws_services.repository.RagRepository;

@Service
public class RagService {

    private final RagRepository ragRepository;

    public RagService(RagRepository ragRepository) {
        this.ragRepository = ragRepository;
    }

    public String askQuestion(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be empty");
        }

        return ragRepository.queryKnowledgeBase(question);
    }
}