package com.seezoon.infrastructure.mcp;

import static org.assertj.core.api.Assertions.assertThat;

import com.seezoon.BaseApplicationTest;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * MCP Client 测试
 *
 * 直接通过 MCP 协议调用 Server 端的工具
 */
@Slf4j
@SpringBootTest
public class McpClientTest extends BaseApplicationTest {


    private McpSyncClient mcpClient;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + 8091;
        log.info("MCP Server URL: {}", baseUrl);

        // 创建 SSE 传输层
        HttpClientStreamableHttpTransport transport = HttpClientStreamableHttpTransport.builder(baseUrl)
                .build();

        // 创建同步 MCP Client
        mcpClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(30))
                .build();

        // 初始化连接
        mcpClient.initialize();
        log.info("MCP Client initialized");
    }

    @AfterEach
    void tearDown() {
        if (mcpClient != null) {
            mcpClient.close();
            log.info("MCP Client closed");
        }
    }

    @Test
    void testListTools() {
        // 列出 Server 端所有可用工具
        McpSchema.ListToolsResult result = mcpClient.listTools();

        log.info("Available tools count: {}", result.tools().size());
        result.tools().forEach(tool -> {
            log.info("Tool: {} - {}", tool.name(), tool.description());
        });

        assertThat(result.tools()).isNotEmpty();
        assertThat(result.tools().stream()
                .map(McpSchema.Tool::name)
                .toList())
                .contains("getWeather");
    }

    @Test
    void testCallGetWeather() {
        // 调用 getWeather 工具
        McpSchema.CallToolResult result = mcpClient.callTool(
                new McpSchema.CallToolRequest("getWeather", Map.of("city", "北京"))
        );

        log.info("getWeather result: {}", result);
        assertThat(result.content()).isNotEmpty();

        // 验证返回内容
        String content = result.content().get(0).toString();
        log.info("Weather content: {}", content);
        assertThat(content).contains("28度");
    }

    @Test
    void testCallGetWeatherWithDifferentCity() {
        // 测试不同城市
        McpSchema.CallToolResult result = mcpClient.callTool(
                new McpSchema.CallToolRequest("getWeather", Map.of("city", "上海"))
        );

        log.info("Shanghai weather: {}", result);
        assertThat(result.content()).isNotEmpty();
    }
}

