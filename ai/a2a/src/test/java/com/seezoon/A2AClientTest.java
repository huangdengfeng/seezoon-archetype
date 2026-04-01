package com.seezoon;

import io.agentscope.core.a2a.agent.A2aAgent;
import io.agentscope.core.a2a.agent.card.AgentCardResolver;
import io.agentscope.core.a2a.agent.card.WellKnownAgentCardResolver;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.core.message.TextBlock;
import java.io.IOException;

public class A2AClientTest {

    public static void main(String[] args) throws IOException {
        // Create agent card resolver by well-known uri.
        AgentCardResolver agentCardResolver =
                WellKnownAgentCardResolver.builder().baseUrl("http://localhost:8090").build();
        // Create A2aAgent
        A2aAgent agent =
                A2aAgent.builder()
                        .name("agentscope-a2a-example-agent")
                        .agentCardResolver(agentCardResolver)
                        .build();
        Msg msg = Msg.builder()
                .role(MsgRole.USER)
                .content(TextBlock.builder().text("What's the weather like in Hangzhou today?").build())
                .build();

//       同步
//        Msg block = agent.call(msg).block();
//        System.out.println(new JsonMapper().writeValueAsString(block));
//        异步
//        Mono<Msg> call = agent.call(msg);
//        call.doOnNext(response ->
//                        System.out.println(new JsonMapper().writeValueAsString(response))
//                )
//                .doOnError(error -> System.out.println(error))
//                .subscribe();
        // stream
        agent.stream(msg)
                .map(
                        event -> {
                            if (event.isLast()) {
                                // The last message is whole artifact message result, which has been
                                // processed and printed in the previous event handling.
                                return "";
                            }
                            Msg message = event.getMessage();
                            StringBuilder partText = new StringBuilder();
                            message.getContent().stream()
                                    .filter(block -> block instanceof TextBlock)
                                    .map(block -> (TextBlock) block)
                                    .forEach(block -> partText.append(block.getText()));
                            return partText.toString();
                        }).doOnNext(s -> {
                    System.out.println(s);
                }).then().block();
        System.in.read();
    }

}
