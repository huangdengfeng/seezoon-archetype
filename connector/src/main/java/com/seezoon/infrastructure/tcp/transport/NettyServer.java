package com.seezoon.infrastructure.tcp.transport;

import com.seezoon.infrastructure.exception.Assertion;
import com.seezoon.infrastructure.tcp.codec.MessageDecoder;
import com.seezoon.infrastructure.tcp.codec.MessageEncoder;
import com.seezoon.infrastructure.tcp.config.ServerConfig;
import com.seezoon.infrastructure.tcp.handler.AccLogHandler;
import com.seezoon.infrastructure.tcp.handler.ConnectionLimitHandler;
import com.seezoon.infrastructure.tcp.handler.DispatcherHandler;
import com.seezoon.infrastructure.tcp.handler.NettyServerHandler;
import com.seezoon.infrastructure.tcp.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    private final ServerConfig config;
    private final DispatcherHandler dispatcher;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerChannel serverChannel;

    public NettyServer(ServerConfig config, DispatcherHandler dispatcher) {
        Assertion.notNull(config, "config must not be null");
        Assertion.notNull(dispatcher, "dispatcher must not be null");
        this.config = config;
        this.dispatcher = dispatcher;
    }

    public void start() {
        log.info("server start config:{}", config);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(config.getBossThreads(), new DefaultThreadFactory("NettyServerBoss"));
        workerGroup = new NioEventLoopGroup(config.getIoThreads(), new DefaultThreadFactory("NettyServerWorker"));

        NettyServerHandler nettyServerHandler = new NettyServerHandler(dispatcher);
        AccLogHandler accLogHandler = new AccLogHandler();
        ConnectionLimitHandler connectionLimitHandler = new ConnectionLimitHandler(config.getMaxConnections());
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        if (log.isDebugEnabled()) {
                            ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));
                        }
                        ch.pipeline().addLast("encode", new MessageEncoder());
                        ch.pipeline().addLast("decode", new MessageDecoder());
                        if (config.getIdleTimeout() > 0) {
                            ch.pipeline().addLast("server-idle",
                                    new IdleStateHandler(0, 0, config.getIdleTimeout(), TimeUnit.SECONDS));
                        }
                        ch.pipeline().addLast("acc", accLogHandler);
                        // 限流
                        ch.pipeline().addLast(connectionLimitHandler);
                        // 共享handle 性能更好，避免有状态成员，需要标记@Sharable 注解，
                        // 每次new 就不是共享，每个链接都会创建handler，不适合高并发
                        ch.pipeline().addLast(nettyServerHandler);
                    }
                }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        ChannelFuture future = bootstrap.bind(config.getIp(), config.getPort()).syncUninterruptibly();
        serverChannel = (ServerChannel) future.channel();
        log.info("NettyServer started ,listen {}", config.getPort());
        // 阻塞等待关闭 , 在spring 容器中不需要
        // future.channel().closeFuture().sync();
    }

    public void stop() {
        if (null != serverChannel) {
            final ChannelFuture channelFuture = serverChannel.closeFuture();
            channelFuture.addListener((f) -> {
                if (!f.isSuccess()) {
                    log.warn("NettyServer close server channel failed", f.cause());
                }
            });
        }
        closeAllSession();
        if (null != bossGroup) {
            bossGroup.shutdownGracefully();
        }
        if (null != workerGroup) {
            workerGroup.shutdownGracefully();
        }
        log.info("NettyServer stoped");
    }


    private void closeAllSession() {
        SessionManager.getInstance().invalidateAll();
    }
}
