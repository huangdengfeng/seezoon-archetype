package com.seezoon.infrastructure.configuration;

import com.seezoon.infrastructure.tcp.transport.NettyServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

@Slf4j
@RequiredArgsConstructor
public class NettyServerSmartLifecycle implements SmartLifecycle {

    private final NettyServer nettyServer;
    private volatile boolean isRunning = false;

    @Override
    public void start() {
        try {
            nettyServer.start();
        } catch (Exception e) {
            log.error("netty server start error", e);
            throw new RuntimeException(e);
        }
        isRunning = true;
    }

    @Override
    public void stop() {
        nettyServer.stop();
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return 0;
    }
}
