package com.seezoon.infrastructure.tcp.handler;

import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.infrastructure.tcp.codec.Cmd;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.codec.Serialization;
import com.seezoon.infrastructure.tcp.codec.Serializer;
import com.seezoon.infrastructure.tcp.common.RpcContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 也可以响应读写事件 ，使用ChannelDuplexHandler
 */
@Slf4j
@Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<ProtocolMessage> {

    private final DispatcherHandler dispatcher;
    private final AtomicInteger sequence = new AtomicInteger(0);


    public NettyClientHandler(DispatcherHandler dispatcher) {
        Assert.notNull(dispatcher, "dispatcher must not be null");
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive channel:{}", ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive channel:{}", ctx.channel());
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ProtocolMessage msg) throws Exception {
        MessageHandler messageHandler = dispatcher.dispatch(msg.getCmd());
        // 收服务端请求包
        if (messageHandler != null) {
            Serializer request = Serialization.deserialize(msg.getBody(), messageHandler.getRequestType());
            if (log.isDebugEnabled()) {
                log.debug("client channelRead request:{}, channel:{},msg:{} ", request, ctx.channel(), msg);
            }
            RpcContext rpcContext = new RpcContext(ctx.channel());
            Serializer response = messageHandler.execute(request, rpcContext);
            if (log.isDebugEnabled()) {
                log.debug("client channelWrite response:{},context:{},msg:{},channel:{}", response, rpcContext, msg,
                        ctx.channel());
            }
            // 回包
            ProtocolMessage protocolMessage = new ProtocolMessage(messageHandler.responseCmd(), response.serialize(),
                    nextSequence());
            ctx.writeAndFlush(protocolMessage);
        } else { // 收服务端响应
            ResponseFuture responseFuture = FutureManager.getInstance()
                    .remove(ctx.channel().id().asLongText(), msg.getRequestId());
            if (responseFuture == null) { // 没有找到消息
                log.error("server channelRead no mapping cmd:{},msg:{},channel:{}", msg.getCmd(), msg, ctx.channel());
                return;
            }
            Object response = Serialization.deserialize(msg.getBody(), responseFuture.getClazz());
            if (log.isDebugEnabled()) {
                log.debug("client channelRead response:{} ,msg:{},channel:{}", response, msg, ctx.channel());
            }
            responseFuture.getFuture().complete(response);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // server will close channel when server don't receive any heartbeat from client util timeout.
        if (evt instanceof IdleStateEvent) {
            // 发送心跳
            Channel channel = ctx.channel();
            ProtocolMessage protocolMessage = new ProtocolMessage(Cmd.C_Ping, new byte[]{0x00, 0x04, 0x04, 0x00, 0x01},
                    nextSequence());
            int timeout = 6000;
            CompletableFuture<ServerRespCO> future = new CompletableFuture<>();
            FutureManager.getInstance().add(channel.id().asLongText(), protocolMessage.getRequestId(),
                    new ResponseFuture(ServerRespCO.class, future, timeout));
            ctx.writeAndFlush(protocolMessage);
            // 使用异步方式处理心跳响应，避免阻塞事件循环
            future.thenAccept(resp -> {
                log.debug("client channel:{} heartbeat response received: {}", channel, resp);
            }).exceptionally(throwable -> {
                log.error("client read heartbeat response error,close chanel:{}", channel, throwable);
                ctx.close();
                return null;
            });
            log.debug("IdleStateEvent triggered, send heartbeat, channel:{}", channel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client exceptionCaught context:{}", ctx, cause);
        ctx.close();
    }


    public int nextSequence() {
        // 无符号short 最大值后，从0 开始
        return sequence.getAndUpdate(current ->
                (current >= 0xFFFF) ? 0 : current + 1
        );
    }
}
