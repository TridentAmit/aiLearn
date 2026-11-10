package com.example.aiLearn.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PromptStuffingController {
    private final ChatClient chatClient;

    @Autowired
    public PromptStuffingController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/newsports")
    public String generate() {
        String context = """
                In frameworks like Spring AI, prompt stuffing usually happens when developers read an entire 
                database table or an entire text file into memory and inject it into a UserMessage. To avoid this, 
                you should look into Vector Stores and Embedding Clients to build a proper RAG workflow, ensuring your 
                prompts stay short, cheap, and lightning-fast.,
                Now tell me about prompt stuffing from above text  1939100100023690  PUNB0193910
                """;

        return chatClient.prompt().user(context).call().content();

    }
}
