package com.seezoon;


import io.agentscope.core.tool.Toolkit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//http://localhost:8090/.well-known/agent-card.json
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    /**
     * Optional, if you want to register tools for the agent.
     */
    @Bean
    public Toolkit toolkit() {
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new ExampleTools());
        return toolkit;
    }
}
