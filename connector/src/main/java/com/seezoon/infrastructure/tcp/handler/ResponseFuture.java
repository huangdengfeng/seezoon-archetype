package com.seezoon.infrastructure.tcp.handler;

import com.seezoon.infrastructure.exception.Assertion;
import io.netty.util.Timeout;
import java.util.concurrent.CompletableFuture;
import lombok.Getter;

@Getter
public class ResponseFuture<T> {

    private final long createTime = System.currentTimeMillis();
    private final Class<T> clazz;
    private final CompletableFuture<T> future;
    /**
     * 超时时间
     */
    private final long timeout;
    private Timeout timeoutFuture;

    /**
     * @param clazz
     * @param future
     * @param timeout 超时时间毫秒
     */
    public ResponseFuture(Class<T> clazz, CompletableFuture<T> future, long timeout) {
        Assertion.notNull(clazz, "clazz is null");
        Assertion.notNull(future, "future is null");
        this.clazz = clazz;
        this.future = future;
        this.timeout = timeout;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime >= timeout;
    }

    public void setTimeoutFuture(Timeout timeoutFuture) {
        this.timeoutFuture = timeoutFuture;
    }
}
