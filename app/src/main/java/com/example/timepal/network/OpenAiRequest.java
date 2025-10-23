package com.example.timepal.network;

import java.util.List;

public class OpenAiRequest {

    private final String model;
    private final List<Message> messages;

    public OpenAiRequest(String model, List<Message> messages) {
        this.model = model;
        this.messages = messages;
    }

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
