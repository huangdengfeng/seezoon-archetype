package com.seezoon.infrastructure.configuration;

import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.grpc.server.autoconfigure.GrpcServerExecutorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC 全局配置
 */
@Slf4j
@Configuration
public class GrpcConfiguration {


    /**
     * 采用协程池，默认为{@link Executors#newCachedThreadPool()}
     *
     * @return
     */
    @Bean
    public GrpcServerExecutorProvider grpcServerExecutorProvider() {
        return () -> Executors.newVirtualThreadPerTaskExecutor();
    }
}

