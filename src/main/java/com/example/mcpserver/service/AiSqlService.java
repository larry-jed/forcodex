package com.example.mcpserver.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AiSqlService {

    private static final String PROMPT_TEMPLATE = "You are a SQL generator. Translate the user question into a safe SQL SELECT statement targeting the 'customers' table. " +
            "Only produce a SQL SELECT without explanation. Question: %s";

    private final ObjectProvider<ChatClient> chatClientProvider;

    public AiSqlService(ObjectProvider<ChatClient> chatClientProvider) {
        this.chatClientProvider = chatClientProvider;
    }

    public Mono<String> generateSql(String question) {
        return Mono.fromCallable(() -> {
            ChatClient chatClient = chatClientProvider.getIfAvailable();
            if (chatClient != null) {
                String prompt = String.format(PROMPT_TEMPLATE, question);
                return chatClient.call(prompt);
            }
            return buildFallbackSql(question);
        }).map(String::trim);
    }

    private String buildFallbackSql(String question) {
        String sanitized = question == null ? "" : question.toLowerCase();
        if (sanitized.contains("city")) {
            return "SELECT id, first_name, last_name, email, city FROM customers ORDER BY city";
        }
        if (sanitized.contains("email")) {
            return "SELECT id, first_name, last_name, email, city FROM customers ORDER BY email";
        }
        return "SELECT id, first_name, last_name, email, city FROM customers";
    }
}
