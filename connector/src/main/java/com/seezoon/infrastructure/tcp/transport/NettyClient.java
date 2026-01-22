package com.seezoon.infrastructure.tcp.transport;

import com.seezoon.application.tcp.dto.ServerRespCO;
import com.seezoon.infrastructure.error.ErrorCode;
import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.exception.ExceptionFactory;
import com.seezoon.infrastructure.tcp.codec.MessageDecoder;
import com.seezoon.infrastructure.tcp.codec.MessageEncoder;
import com.seezoon.infrastructure.tcp.codec.ProtocolMessage;
import com.seezoon.infrastructure.tcp.handler.DispatcherHandler;
import com.seezoon.infrastructure.tcp.handler.FutureManager;
import com.seezoon.infrastructure.tcp.handler.NettyClientHandler;
import com.seezoon.infrastructure.tcp.handler.ResponseFuture;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {

    private final String ip;
    private final int port;
    private final NettyClientHandler nettyClientHandler;
    private final Bootstrap bootstrap;
    private final EventLoopGroup workerGroup;
    private final AtomicInteger sequence = new AtomicInteger(0);
    private volatile Channel channel;
    private int connectionTimeout = 2000;
    private int defaultReadTimeout = 6000;
    // 单位毫秒
    private int heartbeatInterval = 30 * 1000;
    private boolean manualClose;


    public NettyClient(String ip, int port, DispatcherHandler dispatcher) {
        Assertion.notEmpty(ip, "ip must not be empty");
        Assertion.isTrue(port > 0, "port must be > 0");
        Assertion.notNull(dispatcher, "dispatcher must not be null");
        this.ip = ip;
        this.port = port;

        bootstrap = new Bootstrap();
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,
                new DefaultThreadFactory("NettyServerWorker", true));
        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                .channel(NioSocketChannel.class);
        nettyClientHandler = new NettyClientHandler(dispatcher);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                //ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));
                ch.pipeline().addLast("encoder", new MessageEncoder());
                ch.pipeline().addLast("decoder", new MessageDecoder());
                ch.pipeline().addLast("client-idle",
                        new IdleStateHandler(0, 0, heartbeatInterval, TimeUnit.MILLISECONDS));
                ch.pipeline().addLast("handler", nettyClientHandler);
            }
        });
    }

    public void connect() {
        // 启动连接
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(ip, port));
        try {
            // 添加连接状态监听器
            future.addListener(f -> {
                if (f.isSuccess()) {
                    // 添加连接断开监听
                    future.channel().closeFuture().addListener(closeFuture -> {
                        if (!manualClose) {
                            log.warn("Connection lost to {}:{}, will try to reconnect", ip, port);
                            connect();
                        }
                    });
                }
            });
            // 同步等待连接操作完成（成功或失败）
            future.sync();
            this.channel = future.channel();
            log.info("Connect successfully to {}:{}", ip, port);
        } catch (Exception e) {
            // 连接失败处理
            log.error("Connect failed to {}:{}", ip, port, e);
            // 安排重试
            EventLoop loop = bootstrap.config().group().next();
            loop.schedule(() -> {
                connect();
            }, 10, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        manualClose = true;
        if (channel != null) {
            channel.close();
        }
        if (bootstrap != null) {
            workerGroup.shutdownGracefully().syncUninterruptibly();
        }
    }


    public <T> T send(ProtocolMessage msg, Class<T> responseClazz) {
        return this.send(msg, responseClazz, defaultReadTimeout);
    }

    public <T> T send(ProtocolMessage msg, Class<T> responseClazz, long timeout) {
        Assertion.notNull(msg, "msg must not be null");
        Assertion.notNull(responseClazz, "responseClazz must not be null");
        if (channel == null) {
            throw new IllegalStateException("Connect failed ");
        }
        if (!channel.isActive()) {
            log.error("send failed, channel is closed,msg {}", msg);
            throw ExceptionFactory.bizException(ErrorCode.NET_CHANNEL_CLOSED);
        }

        CompletableFuture<T> future = new CompletableFuture<>();
        FutureManager.getInstance().add(channel.id().asLongText(), msg.getRequestId(),
                new ResponseFuture(ServerRespCO.class, future, timeout));
        channel.writeAndFlush(msg);
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("client get response failed to {}:{},msg:{}", ip, port, msg, e);
            throw ExceptionFactory.bizException(ErrorCode.NET_MESSAGE_READ_TIMEOUT);
        }
    }

    public int nextSequence() {
        // 无符号short 最大值后，从0 开始
        return sequence.getAndUpdate(current ->
                (current >= 0xFFFF) ? 0 : current + 1
        );
    }
}
