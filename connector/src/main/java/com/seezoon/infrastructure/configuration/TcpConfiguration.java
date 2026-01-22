package com.seezoon.infrastructure.configuration;

import com.seezoon.infrastructure.properties.AppProperties;
import com.seezoon.infrastructure.tcp.config.ServerConfig;
import com.seezoon.infrastructure.tcp.handler.DispatcherHandler;
import com.seezoon.infrastructure.tcp.handler.MessageHandler;
import com.seezoon.infrastructure.tcp.transport.NettyServer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 扫描服务端程序
 */
@Configuration
@Slf4j
public class TcpConfiguration {

    @Bean
    @Primary
    public DispatcherHandler serverMessageDispatcher(List<MessageHandler> handlers) {
        Map<Short, MessageHandler> handlerMap = new HashMap<>();
        handlers.stream().filter(v -> v.isServerHandler()).forEach(handler -> {
            handlerMap.put(handler.requestCmd(), handler);
        });
        return new DispatcherHandler(handlerMap);
    }

    @Bean("clientDispatcherHandler")
    public DispatcherHandler clientMessageDispatcher(List<MessageHandler> handlers) {
        Map<Short, MessageHandler> handlerMap = new HashMap<>();
        handlers.stream().filter(v -> !v.isServerHandler()).forEach(handler -> {
            handlerMap.put(handler.requestCmd(), handler);
        });
        return new DispatcherHandler(handlerMap);
    }

    @Bean
    public NettyServerSmartLifecycle nettyServerSmartLifecycle(AppProperties appProperties,
            DispatcherHandler dispatcherHandler) {
        ServerConfig serverConfig = appProperties.getServer();
        NettyServer nettyServer = new NettyServer(serverConfig, dispatcherHandler);
        NettyServerSmartLifecycle nettyServerSmartLifecycle = new NettyServerSmartLifecycle(nettyServer);
        return nettyServerSmartLifecycle;
    }

}
