package com.example.mcpserver.model;

import java.util.Collections;
import java.util.Map;

public class McpEvent {

    private final String type;
    private final Object payload;

    private McpEvent(String type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public static McpEvent of(String type, Object payload) {
        Object normalizedPayload = payload;
        if (payload instanceof Map) {
            normalizedPayload = Collections.unmodifiableMap((Map<?, ?>) payload);
        }
        return new McpEvent(type, normalizedPayload);
    }

    public String getType() {
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
