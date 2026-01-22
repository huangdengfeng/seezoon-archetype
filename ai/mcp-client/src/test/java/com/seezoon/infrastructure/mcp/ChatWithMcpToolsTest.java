package com.seezoon.infrastructure.mcp;

import com.seezoon.BaseApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 大模型 + MCP Client 集成测试
 *
 * 通过 MCP Client 连接 MCP Server，让 AI 自动调用远程工具
 *
 * 架构：ChatClient → LLM → MCP Client → HTTP → MCP Server → @McpTool
 */
@Slf4j
@SpringBootTest
public class ChatWithMcpToolsTest extends BaseApplicationTest {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    @Test
    void testChatWithMcpTool() {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();

        log.info("Available MCP tools: {}", toolCallbacks.length);
        for (ToolCallback callback : toolCallbacks) {
            log.info("  - {}", callback.getToolDefinition().name());
        }

        // 让 AI 通过 MCP Client 调用远程工具
        String response = chatClient.prompt()
                .user("北京今天天气怎么样？")
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content();

        log.info("AI Response: {}", response);
    }

    @Test
    void testChatWithoutTool() {
        // 不提供工具，AI 只能根据已有知识回答
        String response = chatClient.prompt()
                .user("北京今天天气怎么样？")
                .call()
                .content();

        log.info("AI Response (no tool): {}", response);
    }

    @Test
    void testChatMultipleCities() {

        // 询问多个城市的天气，AI 会多次调用工具
        String response = chatClient.prompt()
                .user("告诉我北京、上海、广州的天气情况")
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content();

        log.info("AI Response: {}", response);
    }
}

