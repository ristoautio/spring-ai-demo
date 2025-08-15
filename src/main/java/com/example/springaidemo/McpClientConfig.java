package com.example.springaidemo;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;
import java.time.Duration;

@Configuration
@Slf4j
public class McpClientConfig {

    @Bean("mcp-sqlite")
    public McpSyncClient mcpDBClient() {
        var stdioParams = ServerParameters.builder("uvx")
                .args("mcp-sqlite", getPathFor("netflix.db"), "-m", getPathFor("db-meta.yml"))
                .build();

        var mcpClient = McpClient.sync(new StdioClientTransport(stdioParams))
                .requestTimeout(Duration.ofSeconds(10))
                .build();

        //testing error prone
        IllegalArgumentException foo = new IllegalArgumentException();
        var init = mcpClient.initialize();

        log.info("MCP DB Client Initialized: {}", init);

        return mcpClient;
    }

    private static String getPathFor(String fileName) {
        return Paths.get(System.getProperty("user.dir"), fileName).toString();
    }

}
