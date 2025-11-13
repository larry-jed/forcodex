package com.example.mcpserver.model;

public class QueryRequest {

    private String prompt;

    public QueryRequest() {
    }

    public QueryRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
