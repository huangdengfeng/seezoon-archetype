package com.seezoon.infrastructure.llm;

import com.seezoon.BaseApplicationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ChatTest extends BaseApplicationTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    public void hello() {
        String content = chatClient.prompt("你叫什么名字").call().content();
        log.info(content);
    }
}
