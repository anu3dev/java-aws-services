package com.example.java_aws_services.repository;

import org.springframework.stereotype.Repository;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.KnowledgeBaseRetrieveAndGenerateConfiguration;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateRequest;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateResponse;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateType;

@Repository
public class RagRepository {

    private final BedrockAgentRuntimeClient bedrockClient;

    private final String knowledgeBaseId;
    private final String modelId;

    public RagRepository() {

        String region = System.getenv("AWS_REGION");

        this.knowledgeBaseId =
                System.getenv("BEDROCK_KNOWLEDGE_BASE_ID");

        this.modelId =
                System.getenv("BEDROCK_MODEL_ID");

        if (region == null || region.isBlank()) {
            throw new IllegalStateException(
                    "AWS_REGION environment variable is not configured");
        }

        if (knowledgeBaseId == null || knowledgeBaseId.isBlank()) {
            throw new IllegalStateException(
                    "BEDROCK_KNOWLEDGE_BASE_ID environment variable is not configured");
        }

        if (modelId == null || modelId.isBlank()) {
            throw new IllegalStateException(
                    "BEDROCK_MODEL_ID environment variable is not configured");
        }

        this.bedrockClient =
                BedrockAgentRuntimeClient.builder()
                        .region(Region.of(region))
                        .build();
    }

    public String queryKnowledgeBase(String question) {

//        String modelArn =
//                "arn:aws:bedrock:"
//                        + System.getenv("AWS_REGION")
//                        + "::inference-profile/"
//                        + modelId;

        RetrieveAndGenerateRequest request =
                RetrieveAndGenerateRequest.builder()
                        .input(input ->
                                input.text(question))
                        .retrieveAndGenerateConfiguration(
                                config ->
                                        config
                                                .type(RetrieveAndGenerateType.KNOWLEDGE_BASE)
                                                .knowledgeBaseConfiguration(
                                                        KnowledgeBaseRetrieveAndGenerateConfiguration
                                                                .builder()
                                                                .knowledgeBaseId(knowledgeBaseId)
                                                                .modelArn(modelId)
                                                                .build())
                        )
                        .build();

        RetrieveAndGenerateResponse response =
                bedrockClient.retrieveAndGenerate(request);

        if (response.output() == null
                || response.output().text() == null) {

            return "No answer was generated from the Knowledge Base.";
        }

        return response.output().text();
    }
}