package com.projects.ai_support.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;

@Service
public class ChatService {

    private final Client client;
    
    private final ConcurrentHashMap<String, List<String>> conversations =
            new ConcurrentHashMap<>();

    public ChatService() {
        client = new Client();
    }
    
    public String getAnswer(String sessionId, String userMessage) {

        List<String> history = conversations.computeIfAbsent(
                sessionId,
                id -> new ArrayList<>()
        );

        synchronized (history) {

            String conversationContext = String.join("\n", history);

            String prompt = """
                    Previous conversation:
                    %s

                    Current customer message:
                    %s

                    Respond to the current customer message using
                    the previous conversation when relevant.
                    """.formatted(conversationContext, userMessage);

            String answer = getAnswer(prompt);

            history.add("Customer: " + userMessage);
            history.add("Assistant: " + answer);

            return answer;
        }
    }

    public String getAnswer(String userMessage) {

        // 1. Define system instruction
        String systemInstruction = """
                You are an AI customer support assistant.

                Answer customer questions clearly, politely,
                professionally, and helpfully.

                You can answer questions about:
                - Products
                - Orders
                - Returns and refunds
                - Payments
                - Customer support

                Rules:
                - Do not invent company policies.
                - Do not invent order statuses.
                - If information is unavailable, say so.
                - Never ask for passwords or payment card details.
                """;

        // 2. Configure Gemini
        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        .systemInstruction(
                                Content.fromParts(
                                        Part.fromText(systemInstruction)
                                )
                        )
                        .build();

        // 3. Send message to Gemini
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.8-flash",
                        userMessage,
                        config
                );

        // 4. Return AI response
        return response.text();
    }
}
