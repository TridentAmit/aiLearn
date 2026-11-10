package com.example.aiLearn.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final SimpleVectorStore vectorStore;

    // Spring Boot automatically injects your configured SimpleVectorStore bean here
    public RagService(ChatClient.Builder chatClientBuilder, SimpleVectorStore vectorStore) {
        this.vectorStore = vectorStore;
        // Build the chat client fluently
        this.chatClient = chatClientBuilder.build();
    }

    public String askQuestion(String message) {
        // 1. Search for the top 2 matching text chunks in your local vector database
        List<Document> similarDocuments = this.vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(2)
                        .build()
        );


        // 2. Extract plain text content from the retrieved document chunks
        List<String> contentList = similarDocuments.stream()
                .map(Document::getText)
                .toList();


        // 3. Define the rules for the AI model to guarantee it relies on your text file
        String systemPromptTemplate = """
                You are a secure organizational AI assistant. 
                Answer the question using only the provided context. If the context does not 
                contain the answer, politely state that you do not know. Do not make things up.
                
                Context:
                {context}
                """;

        // 4. Augment prompt with context data and make the API call
        return this.chatClient.prompt()
                .system(sp -> sp.text(systemPromptTemplate).param("context", contentList))
                .user(message)
                .call()
                .content();
    }
}

