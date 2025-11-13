package com.example.mcpserver.handler;

import com.example.mcpserver.model.McpEvent;
import com.example.mcpserver.model.QueryRequest;
import com.example.mcpserver.service.AiSqlService;
import com.example.mcpserver.service.DatabaseQueryService;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class QueryStreamHandler {

    private final AiSqlService aiSqlService;
    private final DatabaseQueryService databaseQueryService;

    public QueryStreamHandler(AiSqlService aiSqlService, DatabaseQueryService databaseQueryService) {
        this.aiSqlService = aiSqlService;
        this.databaseQueryService = databaseQueryService;
    }

    public Mono<ServerResponse> streamQuery(ServerRequest request) {
        Mono<QueryRequest> bodyMono = request.bodyToMono(QueryRequest.class)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("query request body is required")));

        Flux<ServerSentEvent<McpEvent>> eventFlux = bodyMono.flatMapMany(queryRequest ->
                aiSqlService.generateSql(queryRequest.getPrompt())
                        .flatMapMany(sql -> Flux.concat(
                                Flux.just(ServerSentEvent.<McpEvent>builder()
                                        .event("sql")
                                        .data(McpEvent.of("sql", sql))
                                        .build()),
                                databaseQueryService.executeQuery(sql)
                                        .index()
                                        .map(tuple2 -> ServerSentEvent.<McpEvent>builder()
                                                .id(String.valueOf(tuple2.getT1()))
                                                .event("row")
                                                .data(McpEvent.of("row", tuple2.getT2()))
                                                .build())
                        ))
                        .switchIfEmpty(Flux.just(ServerSentEvent.<McpEvent>builder()
                                .event("info")
                                .data(McpEvent.of("info", "AI model did not produce a SQL statement"))
                                .build()))
                        .onErrorResume(ex -> Flux.just(ServerSentEvent.<McpEvent>builder()
                                .event("error")
                                .data(McpEvent.of("error", ex.getMessage()))
                                .build())));

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventFlux, ServerSentEvent.class);
    }
}
