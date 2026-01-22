package com.seezoon.infrastructure.tcp.handler;

import com.seezoon.infrastructure.exception.Assertion;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 收回包
 * 如果requestId 是JVM 唯一则不需要chanel,jt808 requestId 是short 循环使用
 */
@Slf4j
public class FutureManager {

    private static final FutureManager futureManager = new FutureManager();
    /**
     * 用于清理过期Future的定时器，512个槽，每个槽1S
     */
    private final static HashedWheelTimer cleanupTimer = new HashedWheelTimer(r -> {
        Thread thread = new Thread(r, "FutureCleanupThread");
        thread.setDaemon(true);
        return thread;
    }, 1, TimeUnit.SECONDS, 512);

    static {
        cleanupTimer.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            cleanupTimer.stop();
        }));
    }

    /**
     * channel+requestId  <-> ResponseFuture
     */
    private Map<String, ResponseFuture> futures = new ConcurrentHashMap<>();

    private FutureManager() {
    }

    public static FutureManager getInstance() {
        return futureManager;
    }

    public void add(String channelId, long requestId, ResponseFuture responseFuture) {
        Assertion.notEmpty(channelId, "channelId not empty");
        Assertion.notNull(responseFuture, "responseFuture is not null");
        String key = this.generateKey(channelId, requestId);
        // 为这个Future添加超时清理任务
        scheduleCleanup(key, responseFuture);
        futures.put(key, responseFuture);

    }

    public ResponseFuture remove(String channelId, long requestId) {
        String key = this.generateKey(channelId, requestId);
        ResponseFuture future = futures.remove(key);
        if (future != null && future.getTimeoutFuture() != null) {
            future.getTimeoutFuture().cancel();
        }
        return future;
    }

    private String generateKey(String channelId, long requestId) {
        Assertion.notEmpty(channelId, "channelId not empty");
        return channelId + "@" + requestId;
    }

    /**
     * 为指定的Future安排清理任务
     */
    private void scheduleCleanup(String key, ResponseFuture responseFuture) {
        long timeoutMs = responseFuture.getTimeout();
        Timeout timeoutFuture = cleanupTimer.newTimeout(timeout -> {
            // 检查Future是否仍然存在且已过期
            ResponseFuture currentFuture = futures.remove(key);
            if (currentFuture != null && currentFuture.isExpired()) {
                log.debug("Cleaned up expired future for key: {}", key);
                // 完成Future并设置超时异常
                currentFuture.getFuture().completeExceptionally(
                        new java.util.concurrent.TimeoutException("Future expired: " + key));
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
        responseFuture.setTimeoutFuture(timeoutFuture);
    }
}
