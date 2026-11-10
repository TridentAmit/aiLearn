package com.example.aiLearn.config;


import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;


@Configuration
public class RagConfiguration {

    @Value("${app.vector-store.path}")
    private String vectorStorePath;

    @Value("classpath:rag-data/company-policy.txt")
    private Resource companyPolicyResource;


    @Bean
    public SimpleVectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File storeFile = new File(vectorStorePath);

        if (storeFile.exists()) {
            System.out.println(">>> [STARTUP] Found existing vector store at relative path: " + storeFile.getAbsolutePath());
            simpleVectorStore.load(storeFile);
            System.out.println(">>> [STARTUP] Vector store successfully loaded into memory!");
        } else {
            System.out.println(">>> [STARTUP] No existing vector store file found. Creating a new one...");
            try {
                // 1. Read raw text document
                TextReader textReader = new TextReader(companyPolicyResource);
                List<Document> documents = textReader.get();

                // 2. Split text into manageable chunks
                TokenTextSplitter splitter = TokenTextSplitter.builder()
                        .withChunkSize(800)          // Number of tokens per chunk (Default is usually around 800)
                        .withMinChunkSizeChars(350)  // Minimum character length
                        .withKeepSeparator(true)     // Keep separation characters intact
                        .build();
                List<Document> splitDocuments = splitter.apply(documents);

                // 3. Generate embeddings and add to store
                simpleVectorStore.add(splitDocuments);
                // 4. Persist to the local relative path JSON file
                simpleVectorStore.save(storeFile);
                System.out.println(">>> [STARTUP] New vector store saved successfully to relative path: " + vectorStorePath);
            } catch (Exception e) {
                throw new RuntimeException("CRITICAL: Failed to initialize and populate vector store during startup", e);
            }
        }

        return simpleVectorStore;
    }
}
