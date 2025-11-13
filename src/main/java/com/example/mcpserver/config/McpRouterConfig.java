package com.example.mcpserver.config;

import com.example.mcpserver.handler.QueryStreamHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class McpRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> mcpRoutes(QueryStreamHandler handler) {
        return RouterFunctions.route()
                .POST("/mcp/query", RequestPredicates.accept(MediaType.APPLICATION_JSON), handler::streamQuery)
                .build();
    }
}
