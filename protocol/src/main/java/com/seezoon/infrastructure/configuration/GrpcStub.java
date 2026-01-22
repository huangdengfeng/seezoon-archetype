package com.seezoon.infrastructure.configuration;

import com.seezoon.stub.greeter.GreeterGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcStub {

    private static final String STUB_GREETER = "greeter";

    @Bean
    public GreeterGrpc.GreeterBlockingStub getGreeterStub(GrpcChannelFactory channels) {
        return GreeterGrpc.newBlockingStub(channels.createChannel(STUB_GREETER));
    }
}
