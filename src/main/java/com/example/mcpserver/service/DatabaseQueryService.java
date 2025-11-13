package com.example.mcpserver.service;

import com.example.mcpserver.persistence.DynamicQueryMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseQueryService {

    private final DynamicQueryMapper dynamicQueryMapper;

    public DatabaseQueryService(DynamicQueryMapper dynamicQueryMapper) {
        this.dynamicQueryMapper = dynamicQueryMapper;
    }

    public Flux<Map<String, Object>> executeQuery(String sql) {
        validate(sql);
        List<Map<String, Object>> rows = dynamicQueryMapper.execute(sql);
        return Flux.fromIterable(rows);
    }

    private void validate(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL statement must not be empty");
        }
        String trimmed = sql.trim().toLowerCase();
        if (!trimmed.startsWith("select")) {
            throw new IllegalArgumentException("Only SELECT statements are allowed");
        }
        if (trimmed.contains(";") || trimmed.contains("--")) {
            throw new IllegalArgumentException("Potentially unsafe SQL detected");
        }
    }
}
