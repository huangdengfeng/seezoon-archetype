package com.seezoon.infrastructure.configuration;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;

/**
 * gRPC 全局异常处理配置
 */
@Slf4j
@Configuration
public class GrpcExceptionConfiguration {

    /**
     * 自定义 trailer key
     */
    private static final Metadata.Key<String> ERROR_CODE_KEY =
            Metadata.Key.of("error_code", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> DETAIL =
            Metadata.Key.of("detail", Metadata.ASCII_STRING_MARSHALLER);

    /**
     * 处理业务异常（IllegalArgumentException）
     */
    @Bean
    GrpcExceptionHandler illegalArgumentExceptionHandler() {
        return exception -> {
            if (exception instanceof IllegalArgumentException) {
                log.warn("gRPC IllegalArgumentException: {}", exception.getMessage());
                // 创建 trailers
                Metadata trailers = new Metadata();
                trailers.put(ERROR_CODE_KEY, "10001");
                trailers.put(DETAIL, exception.getMessage());

                // 返回带 trailers 的 StatusException
                return new StatusException(
                        Status.INVALID_ARGUMENT.withDescription(exception.getMessage()), trailers);
            }
            return null; // 不处理，交给其他 handler
        };
    }

    /**
     * 处理所有未捕获的异常（兜底）
     */
    @Bean
    GrpcExceptionHandler globalExceptionHandler() {
        return exception -> {
            log.error("gRPC unhandled exception", exception);
            return new StatusException(
                    Status.INTERNAL.withDescription("Internal server error")
            );
        };
    }
}

