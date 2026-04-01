package com.seezoon;


import io.agentscope.core.agent.Agent;
import io.agentscope.spring.boot.agui.common.AguiAgentRegistryCustomizer;
import java.net.URI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Bean
    public AguiAgentRegistryCustomizer aguiAgentRegistryCustomizer(Agent agent) {
        return registry -> registry.registerFactory("default", () -> agent);
    }

    @Bean
    public RouterFunction<ServerResponse> staticResourceRouter() {
        return RouterFunctions.route()
                .GET("/", request -> ServerResponse.temporaryRedirect(URI.create("/index.html")).build())
                .resources("/**", new ClassPathResource("static/"))
                .build();
    }
}
