package com.seezoon.infrastructure.tcp.handler;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 消息处理器调度
 */
@Slf4j
public class DispatcherHandler {

    private final Map<Short, MessageHandler> handlerMap;

    public DispatcherHandler(Map<Short, MessageHandler> handlerMap) {
        Assert.notNull(handlerMap, "handlerMap is null");
        this.handlerMap = handlerMap;
    }

    public MessageHandler dispatch(short cmd) {
        MessageHandler handler = handlerMap.get(cmd);
        return handler;
    }
}
