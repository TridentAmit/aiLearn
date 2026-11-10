package com.example.aiLearn.controller;

import com.example.aiLearn.model.YouTuberContainer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @GetMapping("/joke")
    public String generate(@RequestParam(value = "message", defaultValue = "Tell me an Indian joke") String message) {
        return chatClient.prompt().user(message).call().content();

    }

    @GetMapping("/prompt")
    public String simple(){
        return chatClient.prompt("Tell a hindu joke").call().content();
    }

    @GetMapping("/youtube")
    public String findPopularYoutubersByGenre(@RequestParam(value="genre",defaultValue = "tech") String genre){
        SystemMessage systemMessage = new SystemMessage("Tell about only music genre, if some one asks anything else tell them it's out side of their boundry");

        String message = """
                List 10 of the most popular YouTubers in {genre} along with their current subscriber counts,If you don't
                know the answer, just say "I don't know".
                """.replace("{genre}", genre);;
        UserMessage userMessage = new UserMessage(message);
        return chatClient.prompt().messages(systemMessage,userMessage)
                .call().content();
    }

    @GetMapping("/op-parser")
    public List<String> outputParser(){
        SystemMessage systemMessage = new SystemMessage("Give the ans");
        String message = """
                LTell me list of 10 indian scientist
                """;
        UserMessage userMessage = new UserMessage(message);
        return chatClient.prompt().messages(systemMessage,userMessage)
                .call().entity(new ListOutputConverter(new DefaultConversionService()));
    }

    @GetMapping("/map-op-parser")
    public Map<String,Object> outputParserMap(){
        SystemMessage systemMessage = new SystemMessage("Give the ans");
        String message = """
                Tell me list of 10 indian scientist and their major awards, atleast 3 awards
                """;
        UserMessage userMessage = new UserMessage(message);
        return chatClient.prompt().messages(systemMessage,userMessage)
                .call().entity(new MapOutputConverter());
    }

    @GetMapping("/bean-op-parser")
    public YouTuberContainer outputParserBean(){
        String systemMessage = "Give ans";
        String message = """
                List 3 of the most popular YouTubers in {genre}.
                Provide accurate subscriber details and their primary content language.
                """;
        UserMessage userMessage = new UserMessage(message);
        return chatClient.prompt().system(systemMessage)
                .user(u -> u.text(message).param("genre", "music"))
                .call()
                .entity(new BeanOutputConverter<>(YouTuberContainer.class));
    }


}

