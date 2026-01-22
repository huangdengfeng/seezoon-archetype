package com.seezoon.infrastructure.tcp.handler;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端限流
 */
@Slf4j
@Sharable
public class ConnectionLimitHandler extends ChannelInboundHandlerAdapter {

    /**
     * 最大连接数，0 不限制
     */
    private final int maxConnections;
    private final AtomicInteger connections = new AtomicInteger(0);

    public ConnectionLimitHandler(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (maxConnections == 0 ) {
            return;
        }
        int currentConnections = connections.incrementAndGet();
        if (currentConnections > maxConnections) {
            log.debug("server registered reject channel:{} cause exceed max connection limit:{},currentConnections:{}",
                    ctx.channel(), maxConnections, currentConnections);
            ctx.close();
            return;
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        connections.decrementAndGet();
        super.channelUnregistered(ctx);
    }
}
