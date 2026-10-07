//package com.example.java_aws_services.repository;
//
//import org.springframework.stereotype.Repository;
//
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeClient;
//import software.amazon.awssdk.services.bedrockagentruntime.model.KnowledgeBaseRetrieveAndGenerateConfiguration;
//import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateRequest;
//import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateResponse;
//import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateType;
//
//@Repository
//public class RagRepository {
//
//    private final BedrockAgentRuntimeClient bedrockClient;
//
//    private final String knowledgeBaseId;
//    private final String modelId;
//
//    public RagRepository() {
//
//        String region = System.getenv("AWS_REGION");
//
//        this.knowledgeBaseId =
//                System.getenv("BEDROCK_KNOWLEDGE_BASE_ID");
//
//        this.modelId =
//                System.getenv("BEDROCK_MODEL_ID");
//
//        if (region == null || region.isBlank()) {
//            throw new IllegalStateException(
//                    "AWS_REGION environment variable is not configured");
//        }
//
//        if (knowledgeBaseId == null || knowledgeBaseId.isBlank()) {
//            throw new IllegalStateException(
//                    "BEDROCK_KNOWLEDGE_BASE_ID environment variable is not configured");
//        }
//
//        if (modelId == null || modelId.isBlank()) {
//            throw new IllegalStateException(
//                    "BEDROCK_MODEL_ID environment variable is not configured");
//        }
//
//        this.bedrockClient =
//                BedrockAgentRuntimeClient.builder()
//                        .region(Region.of(region))
//                        .build();
//    }
//
//    public String queryKnowledgeBase(String question) {
//
////        String modelArn =
////                "arn:aws:bedrock:"
////                        + System.getenv("AWS_REGION")
////                        + "::inference-profile/"
////                        + modelId;
//
//        RetrieveAndGenerateRequest request =
//                RetrieveAndGenerateRequest.builder()
//                        .input(input ->
//                                input.text(question))
//                        .retrieveAndGenerateConfiguration(
//                                config ->
//                                        config
//                                                .type(RetrieveAndGenerateType.KNOWLEDGE_BASE)
//                                                .knowledgeBaseConfiguration(
//                                                        KnowledgeBaseRetrieveAndGenerateConfiguration
//                                                                .builder()
//                                                                .knowledgeBaseId(knowledgeBaseId)
//                                                                .modelArn(modelId)
//                                                                .build())
//                        )
//                        .build();
//
//        RetrieveAndGenerateResponse response =
//                bedrockClient.retrieveAndGenerate(request);
//
//        if (response.output() == null
//                || response.output().text() == null) {
//
//            return "No answer was generated from the Knowledge Base.";
//        }
//
//        return response.output().text();
//    }
//}



package com.example.java_aws_services.repository;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagentruntime.BedrockAgentRuntimeClient;
import software.amazon.awssdk.services.bedrockagentruntime.model.KnowledgeBaseRetrieveAndGenerateConfiguration;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateRequest;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateResponse;
import software.amazon.awssdk.services.bedrockagentruntime.model.RetrieveAndGenerateType;

@Repository
public class RagRepository {

    private static final Logger log = LoggerFactory.getLogger(RagRepository.class);

    private final BedrockAgentRuntimeClient bedrockClient;
    private final String knowledgeBaseId;
    private final String modelId;

    public RagRepository() {

        String region = System.getenv("AWS_REGION");
        this.knowledgeBaseId = System.getenv("BEDROCK_KNOWLEDGE_BASE_ID");
        this.modelId = System.getenv("BEDROCK_MODEL_ID");

        if (region == null || region.isBlank()) {
            throw new IllegalStateException("AWS_REGION environment variable is not configured");
        }
        if (knowledgeBaseId == null || knowledgeBaseId.isBlank()) {
            throw new IllegalStateException("BEDROCK_KNOWLEDGE_BASE_ID environment variable is not configured");
        }
        if (modelId == null || modelId.isBlank()) {
            throw new IllegalStateException("BEDROCK_MODEL_ID environment variable is not configured");
        }

        log.info("RAG_INIT region={} kbId={} modelId={}", region, knowledgeBaseId, modelId);

        this.bedrockClient = BedrockAgentRuntimeClient.builder()
                .region(Region.of(region))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(20))
                        .apiCallTimeout(Duration.ofSeconds(25))
                        .build())
                .build();
    }

    public String queryKnowledgeBase(String question) {

        RetrieveAndGenerateRequest request = RetrieveAndGenerateRequest.builder()
                .input(input -> input.text(question))
                .retrieveAndGenerateConfiguration(config -> config
                        .type(RetrieveAndGenerateType.KNOWLEDGE_BASE)
                        .knowledgeBaseConfiguration(
                                KnowledgeBaseRetrieveAndGenerateConfiguration.builder()
                                        .knowledgeBaseId(knowledgeBaseId)
                                        .modelArn(modelId)
                                        .build()))
                .build();

        log.info("RAG_CALL_START kbId={} modelArn={}", knowledgeBaseId, modelId);
        long start = System.currentTimeMillis();

        try {
            RetrieveAndGenerateResponse response = bedrockClient.retrieveAndGenerate(request);
            log.info("RAG_CALL_OK ms={}", System.currentTimeMillis() - start);

            if (response.output() == null || response.output().text() == null) {
                return "No answer was generated from the Knowledge Base.";
            }
            return response.output().text();

        } catch (AwsServiceException e) {
            log.error("RAG_AWS_ERROR ms={} code={} status={} requestId={} msg={}",
                    System.currentTimeMillis() - start,
                    e.awsErrorDetails().errorCode(),
                    e.statusCode(),
                    e.requestId(),
                    e.awsErrorDetails().errorMessage());
            throw e;

        } catch (SdkClientException e) {
            // timeouts, connection failures, credential problems
            log.error("RAG_SDK_CLIENT_ERROR ms={} type={} msg={}",
                    System.currentTimeMillis() - start,
                    e.getClass().getName(),
                    e.getMessage(), e);
            throw e;
        }
    }
}