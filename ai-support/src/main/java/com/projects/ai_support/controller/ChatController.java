package com.projects.ai_support.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.ai_support.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {

        String message = request.get("message");
        String sessionId = request.get("sessionId");

        if (message == null || message.isBlank()) {
            return Map.of("error", "Message cannot be empty");
        }

        if (sessionId == null || sessionId.isBlank()) {
            return Map.of("error", "Session ID is required");
        }

        String answer = chatService.getAnswer(sessionId, message);

        return Map.of("answer", answer);
    }
}
