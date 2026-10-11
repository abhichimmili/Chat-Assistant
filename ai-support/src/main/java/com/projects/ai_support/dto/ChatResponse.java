package com.projects.ai_support.dto;

public class ChatResponse {

    private String answer;

    public ChatResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}